package edu.upf.filter;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Optional;

import edu.upf.parser.SimplifiedTweet;

public class FileLanguageFilter implements LanguageFilter{
    final String inputFile;
    final String outputFile;

    public FileLanguageFilter(String i, String o) {

        this.inputFile = i;
        this.outputFile = o;

    }

    @Override
    public void filterLanguage(String language) throws IOException{

        

        try(FileReader reader = new FileReader(inputFile);
            BufferedReader bReader = new BufferedReader(reader);){  // Use try-with-resource to automatically release resources in case of exception             

            File file = new File(outputFile);  
            file.createNewFile();   // createNewFile() creates a new file if and only if a file with this name does not yet exist

            FileWriter writer = new FileWriter(file, true); // Write to the file using the append mode
            BufferedWriter bWriter = new BufferedWriter(writer);   
            
            int line_counter = 0;
            int valid_tweets_counter = 0;

            String line;
            while ((line = bReader.readLine()) != null) {

                Optional<SimplifiedTweet> simplified_tweet = SimplifiedTweet.fromJson(line);

                if (simplified_tweet.isPresent() && simplified_tweet.get().getLanguage().equals(language)) { 

                    bWriter.write(simplified_tweet.get().toString());    // Write one line of content
                    bWriter.newLine();
                    valid_tweets_counter++;
                }

                line_counter++;

            }          
            System.out.println("Number of lines read: "+line_counter+". Number of valid tweets read: "+valid_tweets_counter+"\n");

            bReader.close(); // Close buffered reader and enclosed reader
            bWriter.close(); // Close buffered writer and enclosed writer
            
        } 
        

    }

}

