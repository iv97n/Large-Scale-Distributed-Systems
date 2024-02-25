package edu.upf.uploader;

import java.io.File;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

import com.amazonaws.auth.profile.ProfileCredentialsProvider;
import com.amazonaws.internal.StaticCredentialsProvider;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import com.amazonaws.services.s3.model.PutObjectRequest;


public class S3Uploader implements Uploader {

    private String BucketName;
    private String Prefix;
    private String CredentialProfileName;

    public S3Uploader(String BN, String P, String CPN) {
        this.BucketName = BN;
        this.Prefix = P;
        this.CredentialProfileName = CPN;
    }
    
    @Override
    public void upload(List<String> files) {
        // Create S3 client with specified credentials profile
        AmazonS3 s3Client = AmazonS3ClientBuilder.standard()
                .withCredentials(new ProfileCredentialsProvider(CredentialProfileName))
                .build();

        // Iterate through the list of files and upload them to S3
        for (String file : files) {
            File localFile = new File(file);
            String key = Prefix + "/" + localFile.getName();

            // Upload file to S3 bucket
            s3Client.putObject(new PutObjectRequest(BucketName, key, localFile));
        }

    }

}
