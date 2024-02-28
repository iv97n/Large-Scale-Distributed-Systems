package edu.upf;

import java.util.Arrays;
import org.apache.spark.SparkConf;
import org.apache.spark.api.java.JavaPairRDD;
import org.apache.spark.api.java.JavaRDD;
import org.apache.spark.api.java.JavaSparkContext;
import scala.Tuple2;
import java.util.Optional;

import edu.upf.model.SimplifiedTweet;


public class TwitterLanguageFilterApp {

    public static void main(String[] args) {

        if (args.length < 3) {
            System.err.println("Wrong number of parameters\nUsage: TwitterLanguageFilterApp <language> <output> <inputFile/Folder>");
            System.exit(1);
        }

        String language = args[0];
        String output = args[1];
        String input = args[2];

        // Create a SparkContext to initialize
        SparkConf conf = (new SparkConf()).setAppName("TwitterLanguageFilter");
        
        // conf.set("spark.hadoop.validateOutputSpecs", "false");

        JavaSparkContext sparkContext = new JavaSparkContext(conf);
        // Load input
        JavaRDD<String> json_tweets = sparkContext.textFile(input);

        // Map from raw json tweets to Optional<SimplifiedTweet> instances
        JavaRDD<Optional<SimplifiedTweet>> simplified_tweets = json_tweets.map(tweet -> SimplifiedTweet.fromJson(tweet));
        // Filter the RDD so it only contains valid (non-empty) Optional<SimplifiedTweet> instances
        JavaRDD<Optional<SimplifiedTweet>> valid_simplified_tweets = simplified_tweets.filter(tweet -> tweet.isPresent());
        // Filter the RDD so it only contains tweets with the specified language
        JavaRDD<Optional<SimplifiedTweet>> filtered_tweets = valid_simplified_tweets.filter(tweet -> tweet.get().getLanguage().equals(language));
        // Map from Optional<SimplifiedTweet> instances to the text representation of the tweets
        JavaRDD<String> text_filtered_tweets = filtered_tweets.map(tweet -> tweet.get().toString());

        text_filtered_tweets.saveAsTextFile(output);
        sparkContext.stop();
        
        }
}