package com.nicat.filevaultbackend.configuration;

import io.minio.MinioClient;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@Getter
@Setter
@Slf4j
public class MinIoConfig {
    @Value("${minio.url}")
    private String url;

    @Value("${minio.access.name}")
    private String accessKey;

    @Value("${minio.access.secret}")
    private String accessSecret;

    @Value("${minio.default-buckets}")
    private String[] defaultBuckets;

    @Value("${minio.presignedUrlExpiry}")
    private int presignedUrlExpiry;

    @Bean
    public MinioClient minioClient() {
        return MinioClient
                .builder()
                .endpoint(url)
                .credentials(
                        accessKey,
                        accessSecret
                )
                .build();
    }
}