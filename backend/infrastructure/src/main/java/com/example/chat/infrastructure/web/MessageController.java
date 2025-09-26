package com.example.chat.infrastructure.web;

import com.example.chat.application.usecase.ListMessagesUseCase;
import com.example.chat.application.usecase.PostMessageUseCase;
import com.example.chat.domain.model.Message;
import org.springframework.http.HttpStatus;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/messages")
public class MessageController {
    private final PostMessageUseCase post;
    private final ListMessagesUseCase list;

    public MessageController(PostMessageUseCase post, ListMessagesUseCase list) {
        this.post = post;
        this.list = list;
    }

    @GetMapping
    public List<Message> getAll() {
        return list.listAll();
    }

    static record CreateMessageRequest(@NotBlank @Size(max = 1000) String text) {}

    @PostMapping
    public ResponseEntity<?> create(@Valid @RequestBody CreateMessageRequest req) {
        Message created = post.post(req.text());
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }
}
