package com.example.chat.domain.ports;

import com.example.chat.domain.model.Message;
import java.util.List;

public interface MessageQueryPort {
    List<Message> findAll();
}
