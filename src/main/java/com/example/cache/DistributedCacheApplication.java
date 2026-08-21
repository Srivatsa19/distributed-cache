package com.example.cache;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@EnableScheduling
@SpringBootApplication
public class DistributedCacheApplication {

    public static void main(String[] args) {
        SpringApplication.run(
                DistributedCacheApplication.class,
                args
        );
    }
}
