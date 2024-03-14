package edu.upf.storage;

import java.io.Serializable;
import java.util.Collections;
import java.util.List;

import com.github.tukaaa.model.SimplifiedTweetWithHashtags;

import edu.upf.model.HashTagCount;

public class DynamoHashTagRepository implements IHashtagRepository, Serializable {

  final static String endpoint = "dynamodb.us-east-1.amazonaws.com";
  final static String region = "us-east-1";

  final AmazonDynamoDB client = AmazonDynamoDBClientBuilder.standard()
    .withEndpointConfiguration(
  new AwsClientBuilder.EndpointConfiguration(endpoint, region)
    ).withCredentials(new ProfileCredentialsProvider("upf"))
    .build();

  final DynamoDB dynamoDB = new DynamoDB(client);
  final Table dynamoDBTable = dynamoDB.getTable(LsdsTwitterHashtags);


  @Override
  public void write(SimplifiedTweetWithHashtags h) {
    List<String> hashtags = h.getHashtags();
    
    for (String hashtag: hashtags) {
        
      Item item = dynamoDBTable.getItem(new PrimaryKey("Hashtag", hashtag, "Language", language));

      if (item == null){ //pseudo
        putItemInTable(dynamoDBTable, hashtag, h.getLanguage(), list(h.getTweetId()));
      } else {
        updateItemInTable(dynamoDBTable, hashtag, h.getLanguage(), h.getTweetId());
      }
    }

  }

  @Override
  public List<HashTagCount> readTop10(String lang) {
    return Collections.emptyList(); // TODO IMPLEMENT ME
  }

  public static void putItemInTable(Table dbt, String hashtag, String lan, List<Integer> tweet_id){
    
    try {
      PutItemResponse response = table.putItem(new Item()
              .withPrimaryKey("Hashtag", hashtag, "Language", language)
              .withInt("Counter", 1)
              .withList("TweetIds", tweetIds));

      System.out.println(tableName + " was successfully updated. The request id is "
              + response.sdkHttpResponse().xAmznRequestId());

    } catch (ResourceNotFoundException e) {
      System.err.format("Error: The Amazon DynamoDB table \"%s\" can't be found.\n", tableName);
      System.err.println("Be sure that it exists and that you've typed its name correctly!");
      System.exit(1);
    } catch (DynamoDbException e) {
      System.err.println(e.getMessage());
      System.exit(1);
    }
  }

  public static void updateItemInTable(Table dbt, String hashtag, String lan,  Integer tweet_id){
    
    Map<String, AttributeValue> itemkey = new HashMap<>();
      itemkey.put("Hashtag", AttributeValue.builder().s(hashtag).build());
      itemkey.put("Language", AttributeValue.builder().s(language).build());
    
    Map<String, AttributeValue> updatedAttributes = new HashMap<>();
    //sumar 1 al count i afegir tweet id a la llista

    try {
      UpdateItemRequest request = UpdateItemRequest.builder()
        .tableName(dbt.tableName())
        .key(itemkey)
        .attributeUpdates(updatedAttributes)
        .build();

      dbt.updateItem(request);
      System.out.println("The item in the DynamoDB table was successfully updated!");

    } catch (DynamoDbException e) {
        System.err.println("Unable to update item in DynamoDB table: " + e.getMessage());
        System.exit(1);
    }
    
  }

}
