package com.SmartParking.ai_service.services.listners;

import com.SmartParking.ai_service.dtos.*;
import com.SmartParking.ai_service.exceptions.LlmServiceException;
import com.SmartParking.ai_service.exceptions.MessageProcessingException;
import com.SmartParking.ai_service.exceptions.MessagePublishingException;
import com.SmartParking.ai_service.exceptions.ObjectStorageException;
import com.SmartParking.ai_service.services.llmInteractionServices.LlmService;
import com.SmartParking.ai_service.services.objectStorageServices.ObjectStorageService;
import com.SmartParking.ai_service.services.producers.MessageQueueProducer;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.annotation.KafkaHandler;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;


import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;


@Service
@KafkaListener(topics = "${kakfa.consumer.topic1}", id = "${kafka.consumer.topic1.consumerGroupId1}") // can use concurrency field to spin multiple consumers listening to same topic
public class KafkaConsumer1  implements MessageQueueConsumer{
    // groupid -> consumergroupid (only one consumer of a group gets to read message

    private static final Logger log = LogManager.getLogger(KafkaConsumer1.class);

    private final ObjectMapper objectMapper;
    private final MessageQueueProducer messageQueueProducer;
    private final ObjectStorageService objectStorageService;
    private final String producerTopic;
    private final LlmService llmService;

    public KafkaConsumer1(ObjectMapper objectMapper,
                          MessageQueueProducer messageQueueProducer,
                          ObjectStorageService objectStorageService,
                          @Value("${kafka.producer.topic1}") String producerTopic,
                          LlmService llmService) {
        this.objectMapper = objectMapper;
        this.messageQueueProducer = messageQueueProducer;
        this.objectStorageService = objectStorageService;
        this.producerTopic = producerTopic;
        this.llmService = llmService;
    }


    @KafkaHandler
    public void consume(String data) {
        log.info("Received raw message from Kafka topic {}", producerTopic);
        try {
            MessageUploadEventDto messageUploadEventDto = parseMessage(data);
            log.info("Received message for lot {} with source image {}", messageUploadEventDto.lotId(), messageUploadEventDto.imageUrlOriginal());

            String imageUrl = messageUploadEventDto.imageUrlOriginal();
            byte[] imageData = objectStorageService.downloadFile(imageUrl);
            BoundingBoxes boundingBoxes = messageUploadEventDto.boundingBoxes();
            SlotCountInfo slotCountInfo = messageUploadEventDto.slotInfo().occupiedSlotsCount();

            log.debug("BoundingBoxes: {}", boundingBoxes);
            log.debug("SlotCountInfo: {}", slotCountInfo);

            LlmSlotInfoResponseDto llmSlotInfoResponseDto = llmService.getSlotInfo(imageData,boundingBoxes,slotCountInfo);
            MessageProcessedEventDto res = generateResponse(llmSlotInfoResponseDto,messageUploadEventDto);

            messageQueueProducer.produce(producerTopic, res);
            log.info("Successfully processed message for lot {} and published to topic {}", messageUploadEventDto.lotId(), producerTopic);
        } catch (MessageProcessingException ex) {
            log.error("Failed to process message: {}", ex.getMessage(), ex);
            throw ex;
        } catch (ObjectStorageException | LlmServiceException | MessagePublishingException ex) {
            String message = "Failed to process message due to downstream service error";
            log.error("{}: {}", message, ex.getMessage(), ex);
            throw new MessageProcessingException(message, ex);
        } catch (RuntimeException ex) {
            String message = "Unexpected error during message processing";
            log.error("{}: {}", message, ex.getMessage(), ex);
            throw new MessageProcessingException(message, ex);
        }
    }

    public void saveImage(byte[] imageBytes, String filePath) throws IOException {
        Path path = Paths.get(filePath);
        Files.write(path, imageBytes);
        log.info("Image saved locally at {}", filePath);
    }

    private MessageProcessedEventDto generateResponse(LlmSlotInfoResponseDto llmSlotInfoResponseDto,MessageUploadEventDto messageUploadEventDto){

        // current time in UTC
        String utcNow = ZonedDateTime.now(ZoneId.of("UTC"))
                .format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));


        // slot info both from camera service and llm
        SlotCountInfo availableSlotCOuntInfo = SlotCountInfo.builder()
                .bus(llmSlotInfoResponseDto.bus())
                .car(llmSlotInfoResponseDto.car())
                .motorBike(llmSlotInfoResponseDto.motorBike()).build();

        MessageProcessedEventDto.SlotInfo slotInfo = MessageProcessedEventDto.SlotInfo.builder()
                .occupiedSlotsCount(messageUploadEventDto.slotInfo().occupiedSlotsCount())
                .availableSlotsCount(availableSlotCOuntInfo).build();

        MessageProcessedEventDto.LastUpdated lastUpdated = MessageProcessedEventDto.LastUpdated.builder()
                .cameraService(messageUploadEventDto.lastUpdated().cameraService())
                .aiService(utcNow)
                .build();

        return MessageProcessedEventDto.builder()
                .lotId(messageUploadEventDto.lotId())
                .event(producerTopic)
                .imageUrlOriginal(messageUploadEventDto.imageUrlOriginal())
                .imageUrlProcessed(messageUploadEventDto.imageUrlProcessed())
                .slotInfo(slotInfo)
                .aiDescription(llmSlotInfoResponseDto.description())
                .lastUpdated(lastUpdated)
                .build();
    }

    private MessageUploadEventDto parseMessage(String data) {
        try {
            return objectMapper.readValue(data, MessageUploadEventDto.class);
        } catch (JsonProcessingException ex) {
            throw new MessageProcessingException("Failed to parse inbound message payload", ex);
        }
    }





}