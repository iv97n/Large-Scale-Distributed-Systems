package edu.upf.model;

import java.util.Optional;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import java.io.Serializable;


public class ExtendedSimplifiedTweet implements Serializable {

    private static JsonParser parser = new JsonParser();

    private final Long tweetId; // the id of the tweet (’id’)
    private final String text; // the content of the tweet (’text’)
    private final Long userId; // the user id (’user->id’)
    private final String userName; // the user name (’user’->’name’)
    private final Long followersCount; // the number of followers (’user’->’followers_count’)
    private final String language; // the language of a tweet (’lang’)
    private final boolean isRetweeted; // is it a retweet? (the object ’retweeted_status’ exists?)
    private final Long retweetedUserId; // [if retweeted] (’retweeted_status’->’user’->’id’)
    private final Long retweetedTweetId; // [if retweeted] (’retweeted_status’->’id’)
    private final Long timestampMs; // seconds from epoch (’timestamp_ms’)

    public ExtendedSimplifiedTweet(Long tweetId, String text, Long userId, String userName, Long followersCount, String language, boolean isRetweeted, Long retweetedUserId, Long retweetedTweetId, Long timestampMs) {
        this.tweetId = tweetId;
        this.text = text;
        this.userId = userId;
        this.userName = userName;
        this.followersCount = followersCount;
        this.language = language;
        this.isRetweeted = isRetweeted;
        this.retweetedUserId = 0L;
        this.retweetedTweetId = 0L;
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

            Long tweetId = tweet_as_json_object.get("id").getAsLong();
            Long followersCount = tweet_as_json_object.get("followers_count").getAsLong();
            String text = tweet_as_json_object.get("text").getAsJsonPrimitive().getAsString();
            String language = tweet_as_json_object.get("lang").getAsJsonPrimitive().getAsString();
            Long timestampMs = tweet_as_json_object.get("timestamp_ms").getAsLong();

            JsonObject user_as_json_object = tweet_as_json_object.getAsJsonObject("user");
            Long userId = user_as_json_object.get("id").getAsLong();
            String userName = user_as_json_object.get("name").getAsJsonPrimitive().getAsString();

            boolean isRetweeted = tweet_as_json_object.get("isRetweeted").getAsBoolean();

            Long retweetedUserId = null;
            Long retweetedTweetId = null;

            if (isRetweeted){
                JsonObject retweeted_status = tweet_as_json_object.get("retweeted_status").getAsJsonObject();
                retweetedTweetId = retweeted_status.get("id").getAsLong();
                JsonObject retweeted_user = retweeted_status.get("user").getAsJsonObject();
                retweetedUserId = retweeted_user.get("id").getAsLong();
            }

            ExtendedSimplifiedTweet ext_simplified_tweet = new ExtendedSimplifiedTweet(tweetId, text, userId, userName, followersCount,language,  isRetweeted, retweetedUserId, retweetedTweetId, timestampMs );
            return Optional.of(ext_simplified_tweet);

            } catch (Exception e) {
            // System.out.println("Omitted tweet. Mandatory fields: {\"id\": ,\"text\": , \"user\": {\"id\": , \"name\": }, \"lang\": ,\"timestamp_ms\": }\n");
            return Optional.empty();
            }
    }

    public Long getTweetId(){
    return this.tweetId;
    }
    public String getText(){
        return this.text;
    }
    public Long getUserId(){
        return this.userId;
    }
    public String getUserName(){
        return this.userName;
    }

    public String getLanguage(){
        return this.language;
    }
    public Long getTimeStamp(){
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
    
