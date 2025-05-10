package com.nicat.filevaultbackend;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class FileVaultBackendApplication {
    public static void main(String[] args) {
        SpringApplication.run(FileVaultBackendApplication.class, args);
    }
}