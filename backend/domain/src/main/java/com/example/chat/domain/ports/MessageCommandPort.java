package com.example.chat.domain.ports;

import com.example.chat.domain.model.Message;

public interface MessageCommandPort {
    Message save(Message message);
}
