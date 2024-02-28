package spark;

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
            System.err.println("Usage: TwitterLanguageFilterApp <language> <outputPath> <input>");
            System.exit(1);
        }

        String language = args[0];
        String outputPath = args[1];
        String input = args[2];

        //Create a SparkContext to initialize
        SparkConf conf = new SparkConf().setAppName("Word Count");
        JavaSparkContext sparkContext = new JavaSparkContext(conf);
        // Load input
        JavaRDD<String> sentences = sparkContext.textFile(input);

        JavaRDD<ExtendedSimplifiedTweet> tweets = sentences.map(line -> {
        Optional<ExtendedSimplifiedTweet> optTweet = ExtendedSimplifiedTweet.fromJson(line);
        return optTweet.orElse(null);});
        JavaRDD<ExtendedSimplifiedTweet> filteredTweets = tweets.filter(tweet -> tweet.getLanguage().equals(language) && !tweet.getIsRetweeted());
        JavaRDD<String> filteredTweetstext = filteredTweets.map(tweet -> tweet.getText().toString());

        JavaPairRDD<List<String>, Integer> counts = filteredTweetstext
            .flatMap(s -> {
                String[] words = s.split(" ");
                List<List<String>> bigrams = new ArrayList<>();
                for (int i = 0; i < words.length - 1; i++) {
                    List<String> bigram = new ArrayList<>();
                    bigram.add(normalise(words[i]));
                    bigram.add(normalise(words[i + 1]));
                    bigrams.add(bigram);
                }
                return bigrams.iterator();
            })
            .mapToPair(bigram -> new Tuple2<>(bigram, 1))
            .reduceByKey((a, b) -> a + b);
        //JavaPairRDD<List<String>, Integer> sorted_counts = 
        JavaPairRDD<Integer,List<String>> swapped_counts = counts.mapToPair(pair -> new Tuple2<>(pair._2(), pair._1()));

        //sort by value
        JavaPairRDD<Integer, List<String>> sorted_counts = swapped_counts.sortByKey(false);

        //reorder again
        JavaPairRDD<List<String>, Integer> final_rdd = sorted_counts.mapToPair(pair -> new Tuple2<>(pair._2(), pair._1()));

        //select first 10
        List<Tuple2<List<String>, Integer>> top_ten = final_rdd.take(10);

        // Parallelize the list back into an RDD
        JavaPairRDD<List<String>, Integer> top10RDD = sparkContext.parallelizePairs(top_ten);
        top10RDD.saveAsTextFile(outputPath);
    }

    private static String normalise(String word) {
        return word.trim().toLowerCase();
    }
}
