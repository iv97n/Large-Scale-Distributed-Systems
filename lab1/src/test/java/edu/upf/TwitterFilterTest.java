package edu.upf;

import static org.junit.Assert.assertTrue;

import org.junit.Test;

import edu.upf.filter.FileLanguageFilter;
import edu.upf.parser.SimplifiedTweet;

import java.io.FileReader;
import java.io.BufferedReader;
import java.util.Optional;

/**
 * Unit test for simple App.
 */
public class TwitterFilterTest
{
    
    @Test  
    public void parseValidTweet(){
        try{
            FileReader reader = new FileReader("inputs/test/test1.json");
            BufferedReader bReader = new BufferedReader(reader);
            String line = bReader.readLine();
    
            Optional<SimplifiedTweet> simplified_tweet= SimplifiedTweet.fromJson(line);

            assertTrue("Valid tweet parsed as empty", simplified_tweet.isPresent());
            assertTrue("Tweet id parsing is incorrect", simplified_tweet.get().getTweetId() == 995332494974210048L);
            assertTrue("Text parsing is incorrect", simplified_tweet.get().getText().equals("Dummy text"));
            assertTrue("User id parsing is incorrect", simplified_tweet.get().getUserId() == 492271155);
            assertTrue("User name parsing is incorrect", simplified_tweet.get().getUserName().equals("alba aguirre"));
            assertTrue("Language parsing is incorrect", simplified_tweet.get().getLanguage().equals("es"));
            assertTrue("Timestamp parsing is incorrect", simplified_tweet.get().getTimeStamp() == 10);


        }catch(Exception e){
            System.out.println("An error happened opening the file\n");
        }

        

    }

    @Test
    public void parseInvalidTweet(){
        try{
            FileReader reader = new FileReader("inputs/test/test2.json");
            BufferedReader bReader = new BufferedReader(reader);
            String line = bReader.readLine();
    
            Optional<SimplifiedTweet> simplified_tweet = SimplifiedTweet.fromJson(line);
            assertTrue("Invalid tweet parsed as valid", !simplified_tweet.isPresent());
        }catch(Exception e){
            System.out.println("An error happened opening the file\n");
        }

    }

    @Test
    public void parseTweetWithMissingField(){
        try{
            FileReader reader = new FileReader("inputs/test/test3.json");
            BufferedReader bReader = new BufferedReader(reader);
            String line = bReader.readLine();
    
            Optional<SimplifiedTweet> simplified_tweet = SimplifiedTweet.fromJson(line);
            assertTrue("Invalid tweet parsed as valid", !simplified_tweet.isPresent());
        }catch(Exception e){
            System.out.println("An error happened opening the file\n");
        }

    }
    


    
}
