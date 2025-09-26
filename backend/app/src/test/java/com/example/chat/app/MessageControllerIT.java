package com.example.chat.app;

import java.net.URI;
import java.util.List;
import java.util.Map;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

// Test avec un contexte springboot complet
@ExtendWith(SpringExtension.class) // explicite, évite les surprises
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)  // autodétection de la classe ChatApp qui contient l'annotation @SpringBootApplication
@Testcontainers
@ActiveProfiles("it")
class MessageControllerIT {

    @Container
    @ServiceConnection
    static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:16");

    @LocalServerPort int port;
    @Autowired TestRestTemplate rest;

    @Test
    void create_and_list_messages() {
        Map<String, String> body = Map.of("text", "hello from it");

        ResponseEntity<Map> created = rest.postForEntity(
                URI.create("http://localhost:" + port + "/api/messages"),
                body, Map.class);

        assert created.getStatusCode() == HttpStatus.CREATED;
        assert created.getBody() != null && created.getBody().get("id") != null;

        ResponseEntity<Map[]> list = rest.getForEntity(
                URI.create("http://localhost:" + port + "/api/messages"),
                Map[].class);

        assert list.getStatusCode() == HttpStatus.OK;
        assert list.getBody() != null;
        assert List.of(list.getBody()).stream().anyMatch(m -> "hello from it".equals(m.get("text")));
    }
}