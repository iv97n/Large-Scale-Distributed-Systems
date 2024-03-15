package edu.upf.storage;

import java.io.Serializable;
import java.util.Collections;
import java.util.List;

import com.amazonaws.auth.profile.ProfileCredentialsProvider;
import com.amazonaws.client.builder.AwsClientBuilder;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDB;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClientBuilder;
import com.amazonaws.services.dynamodbv2.document.Table;
import com.amazonaws.services.dynamodbv2.model.ResourceNotFoundException;
import com.amazonaws.services.dynamodbv2.document.DynamoDB;
import com.amazonaws.services.dynamodbv2.document.Item;



import com.github.tukaaa.model.SimplifiedTweetWithHashtags;

//import software.amazon.awssdk.services.dynamodb.model.DynamoDbException;

import java.util.HashMap;
import java.util.Map;
import java.util.List;
import java.util.Arrays;


import edu.upf.model.HashTagCount;

public class DynamoHashTagRepository implements IHashtagRepository, Serializable {

  final static String endpoint = "dynamodb.us-east-1.amazonaws.com";
  final static String region = "us-east-1";

  final AmazonDynamoDB client = AmazonDynamoDBClientBuilder.standard()
    .withEndpointConfiguration(
  new AwsClientBuilder.EndpointConfiguration(endpoint, region)
    ).withCredentials(new ProfileCredentialsProvider("default"))
    .build();

  final DynamoDB dynamoDB = new DynamoDB(client);
  String TableName = "LsdsTwitterHashtags";
  final Table dynamoDBTable = dynamoDB.getTable(TableName);


  @Override
  public void write(SimplifiedTweetWithHashtags h) {
    List<String> hashtags = h.getHashtags();
    
    for (String hashtag: hashtags) {
        
      Item item = dynamoDBTable.getItem("hashtag", hashtag, "language", h.getLanguage());

      if (item == null && h.getLanguage() != null){
        System.out.print(h.getLanguage());
        putItemInTable(dynamoDBTable, hashtag, h.getLanguage(), Arrays.asList(h.getTweetId()));
      } else {
        System.out.print("in");
        updateItemInTable(dynamoDBTable, h.getTweetId(), item);
      }
    }

  }

  @Override
  public List<HashTagCount> readTop10(String lang) {
    return Collections.emptyList(); // TODO IMPLEMENT ME
  }

  public static void putItemInTable(Table dbt, String hashtag, String lan, List<Long> tweet_id){
    
    try {
      dbt.putItem(new Item()
              .withPrimaryKey("hashtag", hashtag, "language", lan)
              .withInt("Counter", 1)
              .withList("TweetIds", tweet_id));

      System.out.println("Table was successfully updated.");

    } catch (ResourceNotFoundException e) {
      System.err.format("Error: The Amazon DynamoDB table can't be found.\n");
      System.err.println("Be sure that it exists and that you've typed its name correctly!");
      System.exit(1);
    } catch (Exception e) {
      System.err.println(e.getMessage());
      System.exit(1);
    }
  }

  public static void updateItemInTable(Table dbt, Long tweet_id, Item item){
    
    try {

      Long cnt = item.getLong("Counter");
      cnt++;
      item.withLong("counter", cnt);
      List<Long> tweetList = item.getList("TweetIds");
      tweetList.add(tweet_id);
      item.withList("TweetIds", tweetList);

      dbt.putItem(item);

      System.out.println("The item in the DynamoDB table was successfully updated!");

    } catch (Exception e) {
        System.err.println("Unable to update item in DynamoDB table: " + e.getMessage());
        System.exit(1);
    }
    
  }

}
