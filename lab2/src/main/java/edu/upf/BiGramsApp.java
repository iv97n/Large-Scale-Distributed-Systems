package edu.upf;

import org.apache.spark.api.java.JavaPairRDD;
import org.apache.spark.api.java.JavaSparkContext;

import edu.upf.model.ExtendedSimplifiedTweet;

import org.apache.spark.api.java.JavaRDD;
import org.apache.spark.SparkConf;
import scala.Tuple2;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;


public class BiGramsApp {
    public static void main(String[] args){

        if (args.length < 3) {
            System.err.println("Usage: BigramsApp <language> <output> <inputFile/Folder>");
            System.exit(1);
        }

        String language = args[0];
        String output = args[1];
        String input = args[2];

        //Create a SparkContext to initialize
        SparkConf conf = new SparkConf().setAppName("BiaGrams");

        JavaSparkContext sparkContext = new JavaSparkContext(conf);
        // Load input
        JavaRDD<String> json_tweets = sparkContext.textFile(input);
        // Map from raw json tweets to Optional<ExtendedSimplifiedTweet> instances
        JavaRDD<Optional<ExtendedSimplifiedTweet>> extended_simplified_tweets = json_tweets.map(tweet -> ExtendedSimplifiedTweet.fromJson(tweet));
        // Filter the RDD so it only contains non-empty and original tweets of the specified language
        JavaRDD<Optional<ExtendedSimplifiedTweet>> filtered_tweets = extended_simplified_tweets.filter(tweet -> tweet.isPresent() && tweet.get().getLanguage().equals(language) && !tweet.get().getIsRetweeted());
        // Map from Optional<ExtendedSimplifiedTweet> instances to strings containing the tweet text
        JavaRDD<String> filtered_tweets_text = filtered_tweets.map(tweet -> tweet.get().getText());
        // Count the number of appearances per bigram
        JavaPairRDD<List<String>, Integer> bigrams_count = filtered_tweets_text
            .flatMap(s -> {
                // Trim the text and split it into words using as delimiter one or more whitespace characters
                String[] words = s.trim().split("\\s+");
                
                List<List<String>> bigrams = new ArrayList<>();
                // Iterate over each pair of consecutive words, appending each bigram to an ArrayList of bigrams
                for (int i = 0; i < words.length - 1; i++) {
                    List<String> bigram = new ArrayList<>();
                    bigram.add(words[i].toLowerCase());
                    bigram.add(words[i + 1].toLowerCase());
                    bigrams.add(bigram);
                }
                return bigrams.iterator();
            })
            .mapToPair(bigram -> new Tuple2<>(bigram, 1)) 
            .reduceByKey((a, b) -> a + b); 

        // Swap the elements of the RDD in order to be able to perform the sorting 
        JavaPairRDD<Integer,List<String>> swapped_bigrams_count = bigrams_count.mapToPair(pair -> new Tuple2<>(pair._2(), pair._1()));

        //Sort by value (number of appearances) in a descending order
        JavaPairRDD<Integer, List<String>> sorted_swapped_bigrams_count = swapped_bigrams_count.sortByKey(false);

        // Swap again the elements of the RDD
        JavaPairRDD<List<String>, Integer> sorted_bigrams_count = sorted_swapped_bigrams_count.mapToPair(pair -> new Tuple2<>(pair._2(), pair._1()));

        // Select the 10 most tweeted bigrams using a List<Tuple2<List<String>, Integer>>
        List<Tuple2<List<String>, Integer>> frequent_bigrams_list = sorted_bigrams_count.take(10);

        // Parallelize the list back into an RDD
        JavaPairRDD<List<String>, Integer> frequent_bigrams = sparkContext.parallelizePairs(frequent_bigrams_list);
        frequent_bigrams.saveAsTextFile(output);
        
        sparkContext.stop();
    }

}
