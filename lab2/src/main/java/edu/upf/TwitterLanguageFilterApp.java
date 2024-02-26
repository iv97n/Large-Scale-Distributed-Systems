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

    public void filterLanguage(String[] args) {

        if (args.length < 3) {
            System.err.println("Usage: TwitterLanguageFilterApp <language> <outputPath> <input>");
            System.exit(1);
        }

        String language = args[0];
        String outputPath = args[1];
        String input = args[2];

        SparkConf conf = (new SparkConf()).setAppName("TwitterLanguageFilter");
        JavaSparkContext sparkContext = new JavaSparkContext(conf);
        JavaRDD<String> sentences = sparkContext.textFile(input);
        JavaRDD<SimplifiedTweet> tweets = sentences.map(line -> {
            Optional<SimplifiedTweet> optTweet = SimplifiedTweet.fromJson(line);
        return optTweet.orElse(null);});
        JavaRDD<SimplifiedTweet> filteredTweets = tweets.filter(tweet -> tweet.getLanguage().equals(language));
        JavaRDD<String> filteredTweetsString = filteredTweets.map(tweet -> tweet.toString());
        filteredTweetsString.saveAsTextFile(outputPath);
        sparkContext.stop();
        
        }
}