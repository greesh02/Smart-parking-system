package com.SmartParking.ai_service.services.listners;

public interface MessageQueueConsumer {
    void consume(String data);
}
