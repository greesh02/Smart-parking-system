package com.SmartParking.ai_service.services.producers;

import com.SmartParking.ai_service.exceptions.MessagePublishingException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.kafka.KafkaException;
import org.springframework.kafka.core.KafkaTemplate;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class KafkaProducerTest {

    private static final String TOPIC = "topic";

    @Mock
    private KafkaTemplate<Object, Object> kafkaTemplate;

    private KafkaProducer kafkaProducer;

    @BeforeEach
    void setUp() {
        kafkaProducer = new KafkaProducer(kafkaTemplate);
    }

    @Test
    @DisplayName("Delegates message publishing to Kafka template")
    void produceDelegatesToTemplate() {
        Object payload = "payload";

        kafkaProducer.produce(TOPIC, payload);

        verify(kafkaTemplate).send(TOPIC, payload);
    }

    @Test
    @DisplayName("Throws MessagePublishingException when Kafka template fails")
    void produceThrowsWhenKafkaFails() {
        doThrow(new KafkaException("failure")).when(kafkaTemplate).send(TOPIC, "payload");

        assertThatThrownBy(() -> kafkaProducer.produce(TOPIC, "payload"))
                .isInstanceOf(MessagePublishingException.class)
                .hasMessageContaining("Failed to publish message");
    }
}

