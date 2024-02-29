package edu.upf.model;

import java.util.Optional;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import java.io.Serializable;


public class ExtendedSimplifiedTweet implements Serializable {

    private static JsonParser parser = new JsonParser();

    private final long tweetId; // the id of the tweet (’id’)
    private final String text; // the content of the tweet (’text’)
    private final long userId; // the user id (’user->id’)
    private final String userName; // the user name (’user’->’name’)
    private final long followersCount; // the number of followers (’user’->’followers_count’)
    private final String language; // the language of a tweet (’lang’)
    private final boolean isRetweeted; // is it a retweet? (the object ’retweeted_status’ exists?)
    private final Long retweetedUserId; // [if retweeted] (’retweeted_status’->’user’->’id’)
    private final Long retweetedTweetId; // [if retweeted] (’retweeted_status’->’id’)
    private final long timestampMs; // seconds from epoch (’timestamp_ms’)

    public ExtendedSimplifiedTweet(long tweetId, String text, long userId, String userName, long followersCount, String language, boolean isRetweeted, Long retweetedUserId, Long retweetedTweetId, long timestampMs) {
        this.tweetId = tweetId;
        this.text = text;
        this.userId = userId;
        this.userName = userName;
        this.followersCount = followersCount; 
        this.language = language;
        this.isRetweeted = isRetweeted; 
        this.retweetedUserId = retweetedUserId; 
        this.retweetedTweetId= retweetedTweetId; 
        this.timestampMs = timestampMs;
    }

    /**
    * Returns a {@link ExtendedSimplifiedTweet} from a JSON String.
    * If parsing fails, for any reason, return an {@link Optional#empty()}
    *
    * @param jsonStr
    * @return an {@link Optional} of a {@link ExtendedSimplifiedTweet}
    */
    public static Optional<ExtendedSimplifiedTweet> fromJson(String jsonStr) {
        try {
            JsonObject tweet_as_json_object = parser.parse(jsonStr).getAsJsonObject();

            long tweetId = tweet_as_json_object.get("id").getAsLong();
            String text = tweet_as_json_object.get("text").getAsJsonPrimitive().getAsString();
            String language = tweet_as_json_object.get("lang").getAsJsonPrimitive().getAsString();
            long timestampMs = tweet_as_json_object.get("timestamp_ms").getAsLong();

            JsonObject user_as_json_object = tweet_as_json_object.getAsJsonObject("user");
            long userId = user_as_json_object.get("id").getAsLong();
            String userName = user_as_json_object.get("name").getAsJsonPrimitive().getAsString();
            long followersCount = user_as_json_object.get("followers_count").getAsLong();

            boolean isRetweeted = tweet_as_json_object.has("retweeted_status");

            Long retweetedUserId = -1L;
            Long retweetedTweetId = -1L;

            if (isRetweeted){
                JsonObject retweeted_status = tweet_as_json_object.getAsJsonObject("retweeted_status");
                retweetedTweetId = retweeted_status.get("id").getAsLong();
                JsonObject retweeted_user = retweeted_status.getAsJsonObject("user");
                retweetedUserId = retweeted_user.get("id").getAsLong();
            }
            
            ExtendedSimplifiedTweet extended_simplified_tweet = new ExtendedSimplifiedTweet(tweetId, text, userId, userName, followersCount, language, isRetweeted, retweetedUserId, retweetedTweetId, timestampMs);
            return = Optional.of(extended_simplified_tweet);

        } 
        
        catch (Exception e) {
            //System.out.println("Omitted tweet. Most likely this is an empty line separating two tweets\n");
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

    public boolean getIsRetweeted(){
        return this.isRetweeted;
    }

    public Long getRetweetedUserId(){
        return this.retweetedUserId;
    }

    public Long getretweetedId(){
        return this.retweetedTweetId;
    }


    @Override
    public String toString() {
        // Overriding how SimplifiedTweets are printed in console or the output file
        // The following line produces valid JSON as output
        return new Gson().toJson(this);
    }
}
    
