package com.example.chat.application.usecase;

import com.example.chat.domain.model.Message;
import com.example.chat.domain.ports.MessageCommandPort;
import org.springframework.stereotype.Service;

import java.time.Instant;

@Service
public class PostMessageUseCase {
    private final MessageCommandPort commandPort;

    public PostMessageUseCase(MessageCommandPort commandPort) {
        this.commandPort = commandPort;
    }

    public Message post(String text) {
        var toSave = new Message(null, text.trim(), Instant.now());
        return commandPort.save(toSave);
    }
}
