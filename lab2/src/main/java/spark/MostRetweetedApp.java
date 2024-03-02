package spark;

import org.apache.spark.api.java.JavaPairRDD;
import org.apache.spark.api.java.JavaSparkContext;
import org.apache.spark.storage.StorageLevel;

import edu.upf.model.ExtendedSimplifiedTweet;
//import edu.upf.model.SimplifiedTweet;


import org.apache.spark.api.java.JavaRDD;
import org.apache.spark.SparkConf;
import scala.Tuple2;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;


public class MostRetweetedApp {
    public static void main(String[] args){

        if (args.length < 2) {
            System.err.println("Usage: TwitterLanguageFilterApp <language> <outputPath> <input>");
            System.exit(1);
        }

        String outputPath = args[0];
        String input = args[1];

        //Create a SparkContext to initialize
        SparkConf conf = new SparkConf().setAppName("MostRetweeted");
        JavaSparkContext sparkContext = new JavaSparkContext(conf);

        // Load input
        JavaRDD<String> json_tweets = sparkContext.textFile(input);

        // Convert the tweets from .json format to Optional<ExtendedSimplifiedTweet> instances
        JavaRDD<Optional<ExtendedSimplifiedTweet>> extended_simplified_tweets = json_tweets.map(tweet -> ExtendedSimplifiedTweet.fromJson(tweet));
        // Filter the RDD so it only contains non-empty retweeted Optional<ExtendedSimplifiedTweet> instances
        JavaRDD<Optional<ExtendedSimplifiedTweet>> filtered_tweets = extended_simplified_tweets.filter(tweet -> tweet.isPresent() && tweet.get().getIsRetweeted());
        // Persist the filtered_tweets RDD since it is used multiple times during the execution
        filtered_tweets.persist(StorageLevel.MEMORY_ONLY());
        // Map from the Optional<ExtendedSimplifiedTweet> instances to the retweeted user id
        JavaRDD<Long> retweeted_users = filtered_tweets.map(tweet -> tweet.get().getRetweetedUserId());
        // Count the number of times each user has been retweeted
        JavaPairRDD<Long, Integer> retweeted_user_count = retweeted_users
            .mapToPair(user -> new Tuple2<>(user, 1))
            .reduceByKey((a, b) -> a + b);
        
        // Order the entries of the RDD by number of retweeted tweets
        JavaPairRDD<Integer,Long> swapped_most_retweeted_users = retweeted_user_count.mapToPair(pair -> new Tuple2<>(pair._2(), pair._1())).sortByKey(false);
        // Select the top 10 most retweeted users
        List<Tuple2<Integer, Long>> top10_user = swapped_most_retweeted_users.take(10);

        //create a list of just top 10 users
        List<Long> top10UserIds = new ArrayList<>();
        for (Tuple2<Integer, Long> tuple : top10_user) {
            top10UserIds.add(tuple._2());
        }  

        // Create empty RDD to append the final answer. Persist it since it is used multiple times during the execution
        JavaRDD<Tuple2<Long, Tuple2<Long, String>>> emptyRDD = sparkContext.emptyRDD();
        JavaPairRDD<Long, Tuple2<Long, String>> top_tweetid_userid= JavaPairRDD.fromJavaRDD(emptyRDD);
        top_tweetid_userid.persist(StorageLevel.MEMORY_ONLY());

        // For each user compute its tweets, count, sort them, and select first one
        for (Long userId : top10UserIds) {
            // Filter the RDD so it only contains retweeted tweets referring to the given user
            JavaRDD<Optional<ExtendedSimplifiedTweet>> user_retweeted_tweets = filtered_tweets.filter(tweet -> tweet.get().getRetweetedUserId().compareTo(userId) == 0);

            // Convert the RDD from single-valued Optional<ExtendedSimplifiedTweet> instances to key-value of the form (key: (retweeted id, retweeted text), value: 1)
            JavaPairRDD<Tuple2<Long, String>, Integer> retweeted_id_pair = user_retweeted_tweets.mapToPair(tweet -> {
                long retweeted_id = tweet.get().getRetweetedId();
                String tweet_text = tweet.get().getText();
                Tuple2<Long, String> key = new Tuple2<>(retweeted_id, tweet_text);
                return new Tuple2<>(key, 1);
            });

            
            // Reduce by key to count the number of times each tweet has been retweeted
            JavaPairRDD<Tuple2<Long, String>, Integer> retweeted_id_count = retweeted_id_pair.reduceByKey((a, b) -> a + b); 
        

            // Swap key and value to obtain a tuple of the form (key: retweeted count, value: (retweeted id, retweeted text))
            JavaPairRDD<Integer, Tuple2<Long, String>> retweeted_id_count_swapped = retweeted_id_count.mapToPair(tuple -> new Tuple2<>(tuple._2(), tuple._1()));

            // Sort by key (retweeted id count) in descending order
            JavaPairRDD<Integer, Tuple2<Long, String>> sorted_retweeted_id_count_swapped = retweeted_id_count_swapped.sortByKey(false);

            // Take the most retweeted tweet
            List<Tuple2<Integer, Tuple2<Long, String>>> most_retweeted_id_count_list = sorted_retweeted_id_count_swapped.take(1);
            
            // Cast the most retweeted tweet to RDD
            JavaRDD<Tuple2<Integer, Tuple2<Long, String>>> most_retweeted_id_swapped = sparkContext.parallelize(most_retweeted_id_count_list);    
            
            // Map the retweeted tweet id RDD into an RDD conaining a tuple (userId, (tweetId, tweetText)) (It is a single-valued RDD, not a key-value RDD)
            JavaPairRDD<Long, Tuple2<Long, String>> most_retweeted_id = most_retweeted_id_swapped.mapToPair(retweeted -> new Tuple2<>(userId, retweeted._2));
                      
            // Append the newly created Tuple to the RDD containing the (tweetid,tweetText) and userid pairs
            top_tweetid_userid  = top_tweetid_userid.union(most_retweeted_id);

        }
        

        
        top_tweetid_userid = top_tweetid_userid.coalesce(1);

        top_tweetid_userid.saveAsTextFile(outputPath);

        // Stop Spark context
        sparkContext.stop();
    }
}
