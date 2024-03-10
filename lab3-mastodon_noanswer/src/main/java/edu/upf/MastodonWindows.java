package edu.upf;

import org.apache.spark.SparkConf;
import org.apache.spark.streaming.Durations;
import org.apache.spark.streaming.StreamingContext;
import org.apache.spark.streaming.dstream.DStream;
import org.apache.spark.streaming.api.java.JavaDStream;
import org.apache.spark.streaming.api.java.JavaPairDStream;
import org.apache.spark.streaming.api.java.JavaStreamingContext;
import org.apache.spark.api.java.JavaRDD;
import org.apache.spark.api.java.JavaPairRDD;
import scala.Tuple2;

import com.github.tukaaa.MastodonDStream;
import com.github.tukaaa.config.AppConfig;
import com.github.tukaaa.model.SimplifiedTweetWithHashtags;
import static edu.upf.util.LanguageMapUtils.buildLanguageMap;

public class MastodonWindows {
        public static void main(String[] args) {
                String input = args[0];

                SparkConf conf = new SparkConf().setAppName("Real-time Mastodon Stateful with Windows Exercise");
                AppConfig appConfig = AppConfig.getConfig();

                StreamingContext sc = new StreamingContext(conf, Durations.seconds(20));
                JavaStreamingContext jsc = new JavaStreamingContext(sc);
                jsc.checkpoint("/tmp/checkpoint");

                JavaRDD<String> maptsv = jsc.sparkContext().textFile(input);
                JavaPairRDD<String, String> language_trans = buildLanguageMap(maptsv);

                JavaDStream<SimplifiedTweetWithHashtags> stream = new MastodonDStream(sc, appConfig).asJStream();              
                JavaDStream<SimplifiedTweetWithHashtags> windowed_stream = stream.window(Durations.seconds(60)); //display the 15 most frequent languages of the last 60 seconds
                

                // Micro-batch processing - Mastodon Stateless
                JavaPairDStream<String, Integer> stream_short_language = stream.mapToPair(tweet -> new Tuple2<>(tweet.getLanguage(), 1));
                JavaPairDStream<String, Tuple2<Integer, String>> stream_joined = stream_short_language.transformToPair(streamRDD -> streamRDD.join(language_trans));
                JavaPairDStream<String, Integer> language_count = stream_joined.mapToPair(pair -> new Tuple2<>(pair._2()._2(), pair._2()._1())).reduceByKey((a,b) -> a+b);
                JavaPairDStream<String, Integer> sorted_language_count = language_count
                        .mapToPair(pair -> new Tuple2<>(pair._2(),pair._1()))
                        .transformToPair(pair -> pair.sortByKey(false))
                        .mapToPair(pair -> new Tuple2<>(pair._2(),pair._1()));

                // Window processing
                JavaPairDStream<String, Integer> windowed_stream_short_language = windowed_stream.mapToPair(tweet -> new Tuple2<>(tweet.getLanguage(), 1));
                JavaPairDStream<String, Tuple2<Integer, String>> windowed_stream_joined = windowed_stream_short_language.transformToPair(streamRDD -> streamRDD.join(language_trans));
                JavaPairDStream<String, Integer> windowed_language_count = windowed_stream_joined.mapToPair(pair -> new Tuple2<>(pair._2()._2(), pair._2()._1())).reduceByKey((a,b) -> a+b);
                /*JavaPairDStream<String, Integer> windowed_language_count = windowed_stream_joined.mapToPair(pair -> new Tuple2<>(pair._2()._2(), pair._2()._1()))
                                                                                                                                .reduceByKeyAndWindow((a,b) -> a+b, 
                                                                                                                                (a, b) -> a - b,
                                                                                                                                Durations.seconds(60), //size of the window
                                                                                                                                Durations.seconds(60) //sliding interval: how many seconds go by before recomputing
                                                                                                                                                      //The computation looks at the last (window_size) seconds every (sliding_interval) seconds
                                                                                                                                );*/


                JavaPairDStream<String, Integer> sorted_windowed_language_count = windowed_language_count
                        .mapToPair(pair -> new Tuple2<>(pair._2(),pair._1()))
                        .transformToPair(pair -> pair.sortByKey(false))
                        .mapToPair(pair -> new Tuple2<>(pair._2(),pair._1()));

                
                
                // Print for Micro-batch
                sorted_language_count.foreachRDD((rdd, time) -> {
                        System.out.println("-------------------------------------------");
                        System.out.println("Time: " + time.milliseconds() + " ms <-- MICRO BATCH");
                        System.out.println("-------------------------------------------");
                        rdd.take(15).forEach(tuple -> System.out.println(tuple));
                        System.out.println();
                });
                
                // Print for Window
                sorted_windowed_language_count.foreachRDD((rdd, time) -> {
                        System.out.println("-------------------------------------------");
                        System.out.println("Time: " + time.milliseconds() + " ms <-- WINDOW");
                        System.out.println("-------------------------------------------");
                        rdd.take(15).forEach(tuple -> System.out.println(tuple));
                        System.out.println();
                });

                // Start the application and wait for termination signal
                sc.start();
                sc.awaitTermination();
        }

}