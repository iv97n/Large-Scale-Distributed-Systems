package edu.upf;

import java.util.List;
import org.apache.spark.SparkConf;
import org.apache.spark.api.java.Optional;
import org.apache.spark.api.java.function.Function2;
import org.apache.spark.streaming.Durations;
import org.apache.spark.streaming.StreamingContext;
import org.apache.spark.streaming.api.java.JavaDStream;
import org.apache.spark.streaming.api.java.JavaPairDStream;
import org.apache.spark.streaming.api.java.JavaStreamingContext;
import com.github.tukaaa.MastodonDStream;
import com.github.tukaaa.config.AppConfig;
import com.github.tukaaa.model.SimplifiedTweetWithHashtags;
import scala.Tuple2;


public class MastodonWithState {
    public static void main(String[] args) throws InterruptedException {
        
        // The language to filter is passed as an argument
        String lang = args[0];


        // Initialisation of stream parameters for monitoring
        SparkConf conf = new SparkConf().setAppName("Real-time Mastodon With State");
        AppConfig appConfig = AppConfig.getConfig();
        StreamingContext sc = new StreamingContext(conf, Durations.seconds(10));
        JavaStreamingContext jsc = new JavaStreamingContext(sc);
        jsc.checkpoint("/tmp/checkpoint");
        JavaDStream<SimplifiedTweetWithHashtags> stream = new MastodonDStream(sc, appConfig).asJStream();


        // Conversion of stream to a pair DStream with user and (language, count)
        JavaPairDStream<Tuple2<String, String>, Integer> userLanguageCounts = stream
        .mapToPair(tweet -> new Tuple2<>(new Tuple2<>(tweet.getUserName(), tweet.getLanguage()), 1))
        .updateStateByKey(updateFunction) // updateFunction is defined below
        .filter(tuple -> tuple._2() > 0); // filter out pairs with count <= 0


        // Filter by the desired language while handling possible null values
        JavaPairDStream<Tuple2<String, String>, Integer> filteredUserLanguageCounts = userLanguageCounts
            .filter(tuple -> {
            try {
                String tweetLanguage = tuple._1()._2();
                if (tweetLanguage != null && tweetLanguage.equalsIgnoreCase(lang)) {
                    return true;
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            return false;
        });


        // Language has already been filtered, we can remove it from the RDD
        JavaPairDStream<String, Integer> filteredUserCounts = filteredUserLanguageCounts.mapToPair(tuple -> new Tuple2<>(tuple._1()._1(), tuple._2()));

        // Sort as we have been doing in previous exercicesm this time leaving the count as the first element to match requested output
        JavaPairDStream<Integer, String> sortedFilteredUserCounts = filteredUserCounts
                        .mapToPair(pair -> new Tuple2<>(pair._2(),pair._1()))
                        .transformToPair(pair -> pair.sortByKey(false))
                        .filter(tuple -> !tuple._2().isEmpty()); //remove the entries in which the name is an empty string --> might be >1 account and lead to incorrect results


        // Print the top 20 users
        sortedFilteredUserCounts.print(20);

        // Start the application and wait for termination signal
        jsc.start();
        jsc.awaitTermination();
        jsc.close();
    }


    // Define the function to update the state
    private static final Function2<List<Integer>, Optional<Integer>, Optional<Integer>> updateFunction =
        (values, state) -> {
            int sum = state.orElse(0); //if no state is detected it is set to 0.
            for (Integer value : values) { //summing up all the values associated with the key.
                sum += value; 
            }
            return Optional.of(sum);
        };

}