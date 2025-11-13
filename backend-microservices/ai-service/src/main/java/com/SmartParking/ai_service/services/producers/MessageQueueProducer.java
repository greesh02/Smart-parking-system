package com.SmartParking.ai_service.services.producers;

public interface MessageQueueProducer {
    void produce(String topic,Object data);
}
