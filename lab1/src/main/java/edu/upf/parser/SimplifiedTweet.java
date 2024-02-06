package edu.upf.parser;

import java.util.Optional;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

public class SimplifiedTweet {

  // All classes use the same instance
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
      JsonElement je = JsonParser.parseString(jsonStr);
      JsonObject jo = je.getAsJsonObject();
      long tweetId = -1;
      String text = null;
      long userId = -1;
      String userName = null;
      String language = null;
      long timestampMs = -1;

      if (jo.has("id")) {
        tweetId = jo.get("id").getAsLong();
      }
      if (jo.has("text")) {
        text = jo.get("text").getAsJsonPrimitive().getAsString();
      }
      if (jo.has("user")) {
        JsonObject userObj = jo.getAsJsonObject("user");
        if (userObj.has("id")) {
          userId = userObj.get("id").getAsLong();
        }
        if (userObj.has("name")) {
          userName = userObj.get("name").getAsJsonPrimitive().getAsString();
        }
      }

      if (jo.has("lang")) {
        language = jo.get("lang").getAsJsonPrimitive().getAsString();
      }
      if (jo.has("timestamp_ms")) {
        timestampMs = jo.get("timestamp_ms").getAsLong();
      }

      if (tweetId == -1 || text == null || userId == -1 || userName == null || language == null || timestampMs == -1) {
        System.out.println("The tweet format is not correct");
        return Optional.empty();
      } else {
        SimplifiedTweet obj = new SimplifiedTweet(tweetId, text, userId, userName, language, timestampMs);
        return Optional.of(obj);
      }
    } catch (Exception e) {
      e.printStackTrace();
      return Optional.empty();
    }
  }


  @Override
  public String toString() {
    return "";
  }
}
