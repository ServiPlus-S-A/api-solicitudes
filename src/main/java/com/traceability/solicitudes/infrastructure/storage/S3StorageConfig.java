package com.traceability.solicitudes.infrastructure.storage;

import com.traceability.solicitudes.infrastructure.config.StorageProperties;
import java.net.URI;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.S3Configuration;
import software.amazon.awssdk.services.s3.model.CreateBucketRequest;
import software.amazon.awssdk.services.s3.model.HeadBucketRequest;

@Configuration
@Profile("jpa")
public class S3StorageConfig {

    @Bean
    public S3Client s3Client(StorageProperties properties) {
        S3Client client = S3Client.builder()
                .endpointOverride(URI.create(properties.endpoint()))
                .region(Region.of(properties.region()))
                .credentialsProvider(StaticCredentialsProvider.create(
                        AwsBasicCredentials.create(properties.accessKey(), properties.secretKey())))
                .serviceConfiguration(S3Configuration.builder().pathStyleAccessEnabled(true).build())
                .build();
        return client;
    }

    private static void ensureBucketExists(S3Client client, String bucket) {
        try {
            client.headBucket(HeadBucketRequest.builder().bucket(bucket).build());
        } catch (Exception ex) {
            client.createBucket(CreateBucketRequest.builder().bucket(bucket).build());
        }
    }
}
