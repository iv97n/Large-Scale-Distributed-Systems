package edu.upf;

import org.apache.spark.SparkConf;
import org.apache.spark.streaming.Durations;
import org.apache.spark.streaming.StreamingContext;
import org.apache.spark.streaming.api.java.JavaDStream;
import org.apache.spark.streaming.api.java.JavaPairDStream;

import org.apache.spark.streaming.api.java.JavaStreamingContext;
import org.apache.spark.api.java.JavaRDD;
import org.apache.spark.api.java.JavaPairRDD;
import org.apache.spark.api.java.JavaSparkContext;

import scala.Tuple2;

import com.github.tukaaa.MastodonDStream;
import com.github.tukaaa.config.AppConfig;
import com.github.tukaaa.model.SimplifiedTweetWithHashtags;

import static edu.upf.util.LanguageMapUtils.buildLanguageMap;

public class MastodonStateless {
        public static void main(String[] args) {
                //String input = args[0];

                SparkConf conf = new SparkConf().setAppName("Real-time Twitter Stateless Exercise");
                AppConfig appConfig = AppConfig.getConfig();
                //JavaSparkContext sparkContext = new JavaSparkContext(conf);

                StreamingContext sc = new StreamingContext(conf, Durations.seconds(10));
                JavaStreamingContext jsc = new JavaStreamingContext(sc);
                jsc.checkpoint("/tmp/checkpoint");

                JavaDStream<SimplifiedTweetWithHashtags> stream = new MastodonDStream(sc, appConfig).asJStream();
                JavaRDD<String> maptsv = jsc.sparkContext().textFile("src/main/resources/map.tsv");
                JavaPairRDD<String, String> language_trans = buildLanguageMap(maptsv);

                JavaPairDStream<String, Integer> stream_short_language = stream.mapToPair(tweet -> new Tuple2<>(tweet.getLanguage(), 1));

                JavaPairDStream<String, Tuple2<Integer, String>> stream_joined = stream_short_language.transformToPair(streamRDD -> streamRDD.join(language_trans));

                JavaPairDStream<String, Integer> language_count = stream_joined.mapToPair(pair -> new Tuple2<>(pair._2()._2(), pair._2()._1())).reduceByKey((a,b) -> a+b);

                JavaPairDStream<String, Integer> sorted_language_count = language_count
                        .mapToPair(pair -> new Tuple2<>(pair._2(),pair._1()))
                        .transformToPair(pair -> pair.sortByKey(false))
                        .mapToPair(pair -> new Tuple2<>(pair._2(),pair._1()));

                sorted_language_count.print(10);

                // Start the application and wait for termination signal
                sc.start();
                sc.awaitTermination();
        }
}