package edu.upf;

import edu.upf.filter.FileLanguageFilter;
import edu.upf.uploader.S3Uploader;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;

public class TwitterFilter {
    public static void main( String[] args ) throws IOException {
        try{
            long startTime = System.nanoTime();

            List<String> argsList = Arrays.asList(args);
            String language = argsList.get(0);
            String outputFile = argsList.get(1);
            String bucket = argsList.get(2);
            System.out.println("Language: " + language + ". Output file: " + outputFile + ". Destination bucket: " + bucket);
            for(String inputFile: argsList.subList(3, argsList.size())) {
                System.out.println("Processing: " + inputFile);
                final FileLanguageFilter filter = new FileLanguageFilter(inputFile, outputFile); 
                filter.filterLanguage(language);
            }
    
            final S3Uploader uploader = new S3Uploader(bucket, "lab1-outputs", "default"); //CPN changed to default and prefix to empty (Done by Jose, Martí and Ivan)
            uploader.upload(Arrays.asList(outputFile));

            long endTime = System.nanoTime();
            long elapsedTime = endTime - startTime;
            System.out.println("Elapsed time: " + (elapsedTime / 1e6) + " milliseconds");
    
        }catch (ArrayIndexOutOfBoundsException e) {
            System.err.println("Invalid parameters\nUsage: <language> <output file> <bucket name> <input file> [<input file>...]");
        }
    }

}
