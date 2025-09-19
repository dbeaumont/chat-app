package com.example.chat.infrastructure.persistence.repo;

import com.example.chat.infrastructure.persistence.entity.MessageEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SpringDataMessageRepository extends JpaRepository<MessageEntity, Long> {}
