package com.example.chat.domain.model;

import java.time.Instant;
import java.util.Objects;

public final class Message {
    private final Long id; // nullable before persistence
    private final String text;
    private final Instant createdAt;

    public Message(Long id, String text, Instant createdAt) {
        this.id = id;
        this.text = Objects.requireNonNull(text);
        this.createdAt = Objects.requireNonNull(createdAt);
    }

    public Long getId() { return id; }
    public String getText() { return text; }
    public Instant getCreatedAt() { return createdAt; }

    public Message withId(Long newId) {
        return new Message(newId, this.text, this.createdAt);
    }
}
