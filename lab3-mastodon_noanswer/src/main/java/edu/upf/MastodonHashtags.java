package edu.upf;

import org.apache.spark.SparkConf;
import org.apache.spark.streaming.Durations;
import org.apache.spark.streaming.StreamingContext;
import org.apache.spark.streaming.api.java.JavaDStream;
import org.apache.spark.streaming.api.java.JavaPairDStream;
import org.apache.spark.streaming.api.java.JavaStreamingContext;

import com.github.tukaaa.MastodonDStream;
import com.github.tukaaa.config.AppConfig;
import com.github.tukaaa.model.SimplifiedTweetWithHashtags;

import org.apache.spark.api.java.JavaRDD;
import org.apache.spark.streaming.api.java.JavaDStream;

import java.util.Arrays;
import java.util.List;

import edu.upf.storage.DynamoHashTagRepository;
import scala.Tuple2;

public class MastodonHashtags {

        public static void main(String[] args) throws InterruptedException {
                SparkConf conf = new SparkConf().setAppName("Real-time Mastodon Hashtags");
                AppConfig appConfig = AppConfig.getConfig();
                StreamingContext sc = new StreamingContext(conf, Durations.seconds(10));
                JavaStreamingContext jsc = new JavaStreamingContext(sc);
                //jsc.checkpoint("/tmp/checkpoint");

                JavaDStream<SimplifiedTweetWithHashtags> stream = new MastodonDStream(sc, appConfig).asJStream();

                stream.foreachRDD(rdd -> {
                        rdd.foreachPartition(partition -> {
                            partition.forEachRemaining(tweet -> {
                                DynamoHashTagRepository repository = new DynamoHashTagRepository();
                                repository.write(tweet);
                            });
                        });
                });
                
                
                // Start the application and wait for termination signal
                jsc.start();
                jsc.awaitTermination();
                jsc.close();
        }
}
