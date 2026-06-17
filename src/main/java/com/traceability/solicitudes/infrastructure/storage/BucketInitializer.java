package com.traceability.solicitudes.infrastructure.storage;

import com.traceability.solicitudes.infrastructure.config.StorageProperties;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.CreateBucketRequest;
import software.amazon.awssdk.services.s3.model.HeadBucketRequest;

@Configuration
@Profile("jpa")
public class BucketInitializer {

    @Bean
    public CommandLineRunner initBucket(S3Client s3Client, StorageProperties properties) {
        return args -> {
            try {
                s3Client.headBucket(HeadBucketRequest.builder().bucket(properties.bucket()).build());
                System.out.println("Bucket ya existe: " + properties.bucket());
            } catch (Exception e) {
                System.out.println("Creando bucket...");
                s3Client.createBucket(CreateBucketRequest.builder().bucket(properties.bucket()).build());
            }
        };
    }
}