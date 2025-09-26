package com.example.chat.infrastructure.persistence;
/*
import com.example.chat.domain.model.Message;
import com.example.chat.infrastructure.persistence.adapter.MessagePersistenceAdapter;
import com.example.chat.infrastructure.persistence.entity.MessageEntity;
import com.example.chat.infrastructure.persistence.repo.SpringDataMessageRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

import java.time.Instant;
*/

import com.example.chat.domain.model.Message;
import com.example.chat.infrastructure.persistence.adapter.MessagePersistenceAdapter;
import com.example.chat.infrastructure.persistence.repo.SpringDataMessageRepository;
import org.springframework.beans.factory.annotation.Autowired;
import com.example.chat.infrastructure.persistence.entity.MessageEntity;
import org.junit.jupiter.api.Test;
import java.time.Instant;

import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

import static org.assertj.core.api.Assertions.assertThat;



/**
 * Unitary JPA test on embedded H2 (fast). No @SpringBootTest here.
 */
@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.ANY)  // force H2
@Import(MessagePersistenceAdapter.class)  // on « câble » l’adapter dans le slice
@ContextConfiguration(classes = MessagePersistenceAdapterTest.Config.class) // ajout d'une classe interne statique pour forcer une mini-configuration spring pour ce slice
@ActiveProfiles("test")  // active application-test.yml
class MessagePersistenceAdapterTest {


    // Context configuration interne pour permettre au context spring de se lancer, à la place de celui présent dans app
    @Configuration
    @EntityScan(basePackageClasses = MessageEntity.class)
    @EnableJpaRepositories(basePackageClasses = SpringDataMessageRepository.class)
    static class Config { }


    @Autowired
    MessagePersistenceAdapter adapter;

    @Test
    void save_and_findAll_with_H2() {
        Message m = new Message(null, "Hello H2", Instant.now());
        var saved = adapter.save(m);
        assertThat(saved.getId()).isNotNull();
        assertThat(adapter.findAll()).extracting(Message::getText).contains("Hello H2");
    }
}