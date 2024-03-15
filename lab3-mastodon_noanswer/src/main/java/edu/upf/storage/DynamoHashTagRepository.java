package edu.upf.storage;

import java.io.Serializable;
import java.util.Collections;
import java.util.List;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.List;
import java.util.Arrays;

import com.amazonaws.auth.profile.ProfileCredentialsProvider;
import com.amazonaws.client.builder.AwsClientBuilder;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDB;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClientBuilder;
import com.amazonaws.services.dynamodbv2.document.Table;
import com.amazonaws.services.dynamodbv2.model.ResourceNotFoundException;
import com.amazonaws.services.dynamodbv2.document.DynamoDB;
import com.amazonaws.services.dynamodbv2.document.Item;
import com.amazonaws.services.dynamodbv2.document.ItemCollection;
import com.amazonaws.services.dynamodbv2.document.ScanOutcome;
import com.github.tukaaa.model.SimplifiedTweetWithHashtags;
import edu.upf.model.HashTagCount;
import edu.upf.model.HashTagCountComparator;

public class DynamoHashTagRepository implements IHashtagRepository, Serializable {

  final static String endpoint = "dynamodb.us-east-1.amazonaws.com";
  final static String region = "us-east-1";

  final AmazonDynamoDB client = AmazonDynamoDBClientBuilder.standard().withEndpointConfiguration( 
    new AwsClientBuilder.EndpointConfiguration(endpoint, region)
    ).withCredentials(new ProfileCredentialsProvider("default"))
  .build();

  final DynamoDB dynamoDB = new DynamoDB(client);
  String TableName = "LsdsTwitterHashtags";
  final Table dynamoDBTable = dynamoDB.getTable(TableName);




  @Override
  public void write(SimplifiedTweetWithHashtags h) {
    
    List<String> hashtags = h.getHashtags();

    if (hashtags != null && h.getLanguage()!= null) {

      for (String hashtag: hashtags) {

        Item item = dynamoDBTable.getItem("hashtag", hashtag, "language", h.getLanguage());
        if (item == null){
          putItemInTable(dynamoDBTable, hashtag, h.getLanguage(), Arrays.asList(h.getTweetId()));
        } else {
          updateItemInTable(dynamoDBTable, h.getTweetId(), item);
        }

      }

    } else {
      System.out.println("No hashtags found or the language was not classified");
    }
  
  }




  @Override
  public List<HashTagCount> readTop10(String lang) {
    Map<String, Long> hashtagCounts = new HashMap<>();

    // Scan the DynamoDB table for items with the specified language
    ItemCollection<ScanOutcome> outcome = dynamoDBTable.scan();
    for (Item item : outcome) {
        String language = item.getString("language");
        
        if (language.equals(lang)) {
            String hashtag = item.getString("hashtag");
            long count = item.getLong("Counter");
            hashtagCounts.put(hashtag, count);
        }
        
    }

    // Sort the hashtags based on their appearance count in descending order
    List<HashTagCount> top10 = hashtagCounts.entrySet().stream()
            .map(entry -> new HashTagCount(entry.getKey(), lang, entry.getValue()))
            .sorted(new HashTagCountComparator().reversed())
            .limit(10)
            .collect(Collectors.toList());

    return top10;
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
      System.exit(1);
    } catch (Exception e) {
      System.err.println(e.getMessage());
    }
  }




  public static void updateItemInTable(Table dbt, Long tweet_id, Item item){
    
    try {

      Long cnt = item.getLong("Counter");
      cnt++;
      item.withLong("Counter", cnt);
      List<Long> tweetList = item.getList("TweetIds");
      tweetList.add(tweet_id);
      item.withList("TweetIds", tweetList);

      dbt.putItem(item);
      System.out.println("The item in the DynamoDB table was successfully updated!");

    } catch (Exception e) {
        System.err.println("Unable to update item in DynamoDB table: " + e.getMessage());
    }
    
  }

  

}
