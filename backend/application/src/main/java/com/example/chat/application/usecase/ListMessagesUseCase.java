package com.example.chat.application.usecase;

import com.example.chat.domain.model.Message;
import com.example.chat.domain.ports.MessageQueryPort;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ListMessagesUseCase {
    private final MessageQueryPort queryPort;

    public ListMessagesUseCase(MessageQueryPort queryPort) {
        this.queryPort = queryPort;
    }

    public List<Message> listAll() {
        return queryPort.findAll();
    }
}
