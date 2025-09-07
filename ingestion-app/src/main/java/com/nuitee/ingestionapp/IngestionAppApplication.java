package com.nuitee.ingestionapp;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication(scanBasePackages = "com.nuitee")
public class IngestionAppApplication {
    public static void main(String[] args) {
        SpringApplication.run(IngestionAppApplication.class, args);
    }
}
