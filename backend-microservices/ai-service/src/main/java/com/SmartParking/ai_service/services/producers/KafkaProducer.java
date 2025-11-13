package com.SmartParking.ai_service.services.producers;

import com.SmartParking.ai_service.exceptions.MessagePublishingException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.kafka.KafkaException;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
public class KafkaProducer implements MessageQueueProducer {

    private static final Logger log = LogManager.getLogger(KafkaProducer.class);

    private final KafkaTemplate<Object, Object> template;

    public KafkaProducer(KafkaTemplate<Object, Object> template) {
        this.template = template;
    }

    @Override
    public void produce(String topic,Object data){
        try {
            log.info("Publishing to topic {}: {}", topic, data != null ? data.getClass().getSimpleName() : "null");
            template.send(topic,data);
        } catch (KafkaException ex) {
            String message = "Failed to publish message to topic " + topic;
            log.error("{}: {}", message, ex.getMessage(), ex);
            throw new MessagePublishingException(message, ex);
        }
    }
}

