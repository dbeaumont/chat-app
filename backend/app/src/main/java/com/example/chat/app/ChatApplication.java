package com.example.chat.app;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@SpringBootApplication(scanBasePackages = "com.example.chat")
@EntityScan(basePackages = "com.example.chat.infrastructure.persistence.entity")
@EnableJpaRepositories(basePackages = "com.example.chat.infrastructure.persistence.repo")
public class ChatApplication {
    public static void main(String[] args) {
        SpringApplication.run(ChatApplication.class, args);
    }
}


