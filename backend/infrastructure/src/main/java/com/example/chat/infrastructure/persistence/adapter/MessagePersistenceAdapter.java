package com.example.chat.infrastructure.persistence.adapter;

import com.example.chat.domain.model.Message;
import com.example.chat.domain.ports.MessageCommandPort;
import com.example.chat.domain.ports.MessageQueryPort;
import com.example.chat.infrastructure.persistence.entity.MessageEntity;
import com.example.chat.infrastructure.persistence.repo.SpringDataMessageRepository;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component
public class MessagePersistenceAdapter implements MessageCommandPort, MessageQueryPort {
    private final SpringDataMessageRepository repo;

    public MessagePersistenceAdapter(SpringDataMessageRepository repo) {
        this.repo = repo;
    }

    @Override
    public Message save(Message message) {
        MessageEntity e = new MessageEntity();
        e.setText(message.getText());
        e.setCreatedAt(message.getCreatedAt());
        e = repo.save(e);
        return message.withId(e.getId());
    }

    @Override
    public List<Message> findAll() {
        return repo.findAll().stream()
                .sorted((a,b) -> a.getCreatedAt().compareTo(b.getCreatedAt()))
                .map(e -> new Message(e.getId(), e.getText(), e.getCreatedAt()))
                .collect(Collectors.toList());
    }
}
