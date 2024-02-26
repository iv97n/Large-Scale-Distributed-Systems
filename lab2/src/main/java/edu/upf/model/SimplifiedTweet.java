package edu.upf.model;

import java.util.Optional;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

public class SimplifiedTweet {

  // All instance of the SimplifiedTweet class use the same JsonParser instance
  private static JsonParser parser = new JsonParser();


  private final long tweetId;			  // the id of the tweet ('id')
  private final String text;  		      // the content of the tweet ('text')
  private final long userId;			  // the user id ('user->id')
  private final String userName;		  // the user name ('user'->'name')
  private final String language;          // the language of a tweet ('lang')
  private final long timestampMs;		  // seconduserIds from epoch ('timestamp_ms')

  public SimplifiedTweet(long tweetId, String text, long userId, String userName, String language, long timestampMs) {
    this.tweetId = tweetId;
    this.text = text;
    this.userId = userId;
    this.userName = userName;
    this.language = language;
    this.timestampMs = timestampMs;
  }

  /**
   * Returns a {@link SimplifiedTweet} from a JSON String.
   * If parsing fails, for any reason, return an {@link Optional#empty()}
   *
   * @param jsonStr
   * @return an {@link Optional} of a {@link SimplifiedTweet}
   */
  public static Optional<SimplifiedTweet> fromJson(String jsonStr) {
    try {
      JsonObject tweet_as_json_object = parser.parse(jsonStr).getAsJsonObject();

      long tweetId = tweet_as_json_object.get("id").getAsLong();
      String text = tweet_as_json_object.get("text").getAsJsonPrimitive().getAsString();
      String language = tweet_as_json_object.get("lang").getAsJsonPrimitive().getAsString();
      long timestampMs = tweet_as_json_object.get("timestamp_ms").getAsLong();

      JsonObject user_as_json_object = tweet_as_json_object.getAsJsonObject("user");
      long userId = user_as_json_object.get("id").getAsLong();
      String userName = user_as_json_object.get("name").getAsJsonPrimitive().getAsString();

      SimplifiedTweet simplified_tweet = new SimplifiedTweet(tweetId, text, userId, userName, language, timestampMs);
      return Optional.of(simplified_tweet);

    } catch (Exception e) {
      // System.out.println("Omitted tweet. Mandatory fields: {\"id\": ,\"text\": , \"user\": {\"id\": , \"name\": }, \"lang\": ,\"timestamp_ms\": }\n");
      return Optional.empty();
    }
  }

   
  public long getTweetId(){
    return this.tweetId;
  }
  public String getText(){
    return this.text;
  }
  public long getUserId(){
    return this.userId;
  }
  public String getUserName(){
    return this.userName;
  }

  public String getLanguage(){
    return this.language;
  }
  public long getTimeStamp(){
    return this.timestampMs;
  }


  @Override
  public String toString() {
    // Overriding how SimplifiedTweets are printed in console or the output file
    // The following line produces valid JSON as output
    return new Gson().toJson(this);
  }
}
