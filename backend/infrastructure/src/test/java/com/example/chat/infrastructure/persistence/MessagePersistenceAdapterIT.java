package com.example.chat.infrastructure.persistence;

import com.example.chat.domain.model.Message;
import com.example.chat.infrastructure.persistence.adapter.MessagePersistenceAdapter;
import com.example.chat.infrastructure.persistence.entity.MessageEntity;
import com.example.chat.infrastructure.persistence.repo.SpringDataMessageRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.time.Instant;

import static org.assertj.core.api.Assertions.assertThat;



// Test avec non pas un contexte springboot complet, mais un SLICE de contexte, généré par DataJpaTest
@ExtendWith(SpringExtension.class)
@Testcontainers
@DataJpaTest
@EntityScan(basePackageClasses = MessageEntity.class)
@EnableJpaRepositories(basePackageClasses = SpringDataMessageRepository.class)
@Import(MessagePersistenceAdapter.class)
class MessagePersistenceAdapterIT {

    // Context configuration interne pour permettre au context spring de se lancer, à la place de celui présent dans app
    @Configuration
    static class Config { }

    @Container
    static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:16");

    @DynamicPropertySource
    static void configure(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", postgres::getJdbcUrl);
        registry.add("spring.datasource.username", postgres::getUsername);
        registry.add("spring.datasource.password", postgres::getPassword);
        registry.add("spring.jpa.hibernate.ddl-auto", () -> "create");
    }

    @Autowired
    MessagePersistenceAdapter adapter;

    @Test
    void save_and_list_in_postgres() {
        Message m = new Message(null, "hello", Instant.now());
        Message saved = adapter.save(m);
        assertThat(saved.getId()).isNotNull();
        assertThat(adapter.findAll()).extracting(Message::getText).contains("hello");
    }
}
