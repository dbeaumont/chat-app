package com.example.chat.application.usecase;

import com.example.chat.domain.model.Message;
import com.example.chat.domain.ports.MessageCommandPort;
import org.junit.jupiter.api.Test;

import java.time.Instant;

import static org.assertj.core.api.Assertions.assertThat;

class PostMessageUseCaseTest {

    @Test
    void should_create_message_with_trim_and_timestamp() {
        MessageCommandPort dummy = m -> m.withId(1L);
        PostMessageUseCase usecase = new PostMessageUseCase(dummy);

        Message saved = usecase.post("  hello  ");

        assertThat(saved.getId()).isEqualTo(1L);
        assertThat(saved.getText()).isEqualTo("hello");
        assertThat(saved.getCreatedAt()).isNotNull();
        assertThat(saved.getCreatedAt()).isBeforeOrEqualTo(Instant.now());
    }
}
