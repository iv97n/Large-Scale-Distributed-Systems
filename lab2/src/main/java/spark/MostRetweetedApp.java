package spark;

import org.apache.spark.api.java.JavaPairRDD;
import org.apache.spark.api.java.JavaSparkContext;

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

        if (args.length < 3) {
            System.err.println("Usage: TwitterLanguageFilterApp <language> <outputPath> <input>");
            System.exit(1);
        }

        String outputPath = args[0];
        String input = args[1];

        //Create a SparkContext to initialize
        SparkConf conf = new SparkConf().setAppName("MostRetweeted");
        JavaSparkContext sparkContext = new JavaSparkContext(conf);
        // Load input
        JavaRDD<String> sentences = sparkContext.textFile(input);

        // Filter the valid tweets
        JavaRDD<Optional<ExtendedSimplifiedTweet>> tweets = sentences.map(tweet -> ExtendedSimplifiedTweet.fromJson(tweet));
        JavaRDD<Optional<ExtendedSimplifiedTweet>> valid_tweets = tweets.filter(tweet -> tweet.isPresent());

        // Get retweeted users count
        JavaRDD<Optional<ExtendedSimplifiedTweet>> filteredTweets = tweets.filter(tweet -> tweet.get().getIsRetweeted());
        JavaRDD<Long> retweeted_users = filteredTweets.map(tweet -> tweet.get().getRetweetedUserId());
        JavaPairRDD<Long, Integer> retweeted_user_count = retweeted_users
            .mapToPair(user -> new Tuple2<>(user, 1))
            .reduceByKey((a, b) -> a + b);
        
        //Order users and select first 10
        JavaPairRDD<Integer,Long> swapped_most_retweeted_users = retweeted_user_count.mapToPair(pair -> new Tuple2<>(pair._2(), pair._1())).sortByKey(false);
        List<Tuple2<Integer, Long>> top10_user = swapped_most_retweeted_users.take(10);

        //create a list of just top 10 users
        List<Long> top10UserIds = new ArrayList<>();
        for (Tuple2<Integer, Long> tuple : top10_user) {
            top10UserIds.add(tuple._2());
        }

        //Filter only retweeted tweets that make reference to a user in the top 10 list
        JavaRDD<Optional<ExtendedSimplifiedTweet>> top10_user_retweeted = valid_tweets
        .filter(tweet -> tweet.get().getIsRetweeted() && top10UserIds.contains(tweet.get().getRetweetedUserId()));   
          
        JavaRDD<Tuple2<Long, Long>> top10_id_to_user = sparkContext.emptyRDD();

        //for each user compute its tweets, count, sort them, and select first one
        for (Long userId : top10UserIds) {
            //Get tweets of refering to each user in the iteration
            JavaRDD<Optional<ExtendedSimplifiedTweet>> tweetsForUser = top10_user_retweeted.filter(tweet -> tweet.get().getRetweetedUserId() == userId);
            // Count appearances of such tweets 
            JavaPairRDD<Long, Integer> tweet_count = tweetsForUser.mapToPair(tweet -> {
                long tweetId = tweet.get().getTweetId();
                return new Tuple2<>(tweetId, 1);
            });

            // Reduce by key to count
            JavaPairRDD<Long, Integer> tweetIdCountRDD = tweet_count.reduceByKey((a, b) -> a + b);      

            // Swapp key and value
            JavaPairRDD<Integer, Long> tweetIdCountRDD_swapped = tweetIdCountRDD.mapToPair(tuple -> new Tuple2<>(tuple._2(), tuple._1()));

            // Sort by key (count) in descending order
            JavaPairRDD<Integer, Long> sortedtweetIdCountRDD = tweetIdCountRDD_swapped.sortByKey(false);

            // Take the most retweeted tweed
            List<Tuple2<Integer, Long>> mostRetweetedTweet = sortedtweetIdCountRDD.take(1);

            // Convert the list of top 1 into an RDD
            JavaRDD<Tuple2<Integer, Long>> mostRetweetedTweetRDD = sparkContext.parallelize(mostRetweetedTweet);       
            
            // Create RDD with (tweetid, userid)
            JavaRDD<Tuple2<Long, Long>> userTweet = mostRetweetedTweetRDD.map(tuple -> new Tuple2(tuple._2(), userId));
                      
            //Append 
            top10_id_to_user = top10_id_to_user.union(userTweet);

        }
        
  
        // RDD with key = tweetid, value = userid
        JavaPairRDD<Long,Long> paired_top10_retweets = top10_id_to_user.mapToPair(pair -> new Tuple2<>(pair._1(), pair._2()));
        
// FINS AQUÍ VA TOT BÉ EN TEORIA, QUEDA FER RETURN DEL ID + EL TWEET (COM A CLASSE O COM A TEXT. OMAÑA HA DIT TEXT) 
        // S'HA DE PASSAR DE (TWEETID, USERID) A (TWEETID, TEXT). AMB JOIN SE'NS COMPLICAVA I SORTIA OUTPUTS REPETITS. 
        //LO DE BAIX NO ESTÀ EXECUTAT ENCARA AIXÍ QUE NS SI VA
        
        // Select tweetid of top 10 most retweeted users as a list
        List<Long> tweet_top10 = paired_top10_retweets.map(value -> value._1()).collect();

        // Rdd (tweetid, text) 
        JavaPairRDD<Long, String> top10_id_to_text = top10_user_retweeted.filter(tweet -> tweet_top10.contains(tweet.get().getRetweetedUserId())).mapToPair(tweet -> new Tuple2<>(tweet.get().getTweetId(), tweet.get().getText()));
        
        // Save RDD in one single file
        top10_id_to_text = top10_id_to_text.coalesce(1);

        top10_id_to_text.saveAsTextFile(outputPath);
        
        // Stop Spark context
        sparkContext.stop();
    }
}
