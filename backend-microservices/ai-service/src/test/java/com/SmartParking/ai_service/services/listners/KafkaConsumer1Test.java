package com.SmartParking.ai_service.services.listners;

import com.SmartParking.ai_service.dtos.BoundingBoxes;
import com.SmartParking.ai_service.dtos.LlmSlotInfoResponseDto;
import com.SmartParking.ai_service.dtos.MessageProcessedEventDto;
import com.SmartParking.ai_service.dtos.MessageUploadEventDto;
import com.SmartParking.ai_service.dtos.SlotCountInfo;
import com.SmartParking.ai_service.exceptions.MessageProcessingException;
import com.SmartParking.ai_service.services.llmInteractionServices.LlmService;
import com.SmartParking.ai_service.services.objectStorageServices.ObjectStorageService;
import com.SmartParking.ai_service.services.producers.MessageQueueProducer;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class KafkaConsumer1Test {

    private static final String PRODUCER_TOPIC = "messageProcessed";

    @Mock
    private ObjectMapper objectMapper;

    @Mock
    private MessageQueueProducer messageQueueProducer;

    @Mock
    private ObjectStorageService objectStorageService;

    @Mock
    private LlmService llmService;

    private KafkaConsumer1 kafkaConsumer1;

    @BeforeEach
    void setUp() {
        kafkaConsumer1 = new KafkaConsumer1(
                objectMapper,
                messageQueueProducer,
                objectStorageService,
                PRODUCER_TOPIC,
                llmService
        );
    }

    @Test
    @DisplayName("Consumes and publishes processed message when upstream services succeed")
    void consumePublishesProcessedMessage() throws Exception {
        String payload = "payload";
        MessageUploadEventDto.SlotInfo incomingSlotInfo = new MessageUploadEventDto.SlotInfo(
                SlotCountInfo.builder().car(1).motorBike(2).bus(0).build()
        );
        MessageUploadEventDto.LastUpdated lastUpdated = new MessageUploadEventDto.LastUpdated("camera");
        BoundingBoxes boundingBoxes = new BoundingBoxes(List.of(
                new BoundingBoxes.OccupiedSlot(List.of(1, 2, 3, 4), "car", 0.9f)
        ));
        MessageUploadEventDto uploadEventDto = new MessageUploadEventDto(
                "event",
                "lot-1",
                "original-image",
                "processed-image",
                incomingSlotInfo,
                boundingBoxes,
                lastUpdated
        );
        LlmSlotInfoResponseDto llmResponse = new LlmSlotInfoResponseDto(2, 4, 1, "all good");

        when(objectMapper.readValue(payload, MessageUploadEventDto.class)).thenReturn(uploadEventDto);
        when(objectStorageService.downloadFile("original-image")).thenReturn("bytes".getBytes());
        when(llmService.getSlotInfo(any(), eq(boundingBoxes), eq(incomingSlotInfo.occupiedSlotsCount())))
                .thenReturn(llmResponse);

        kafkaConsumer1.consume(payload);

        ArgumentCaptor<MessageProcessedEventDto> captor = ArgumentCaptor.forClass(MessageProcessedEventDto.class);
        verify(messageQueueProducer).produce(eq(PRODUCER_TOPIC), captor.capture());

        MessageProcessedEventDto processedEvent = captor.getValue();
        assertThat(processedEvent).isNotNull();
        assertThat(processedEvent.lotId()).isEqualTo("lot-1");
        assertThat(processedEvent.slotInfo().availableSlotsCount().car()).isEqualTo(2);
        assertThat(processedEvent.slotInfo().availableSlotsCount().motorBike()).isEqualTo(4);
        assertThat(processedEvent.slotInfo().availableSlotsCount().bus()).isEqualTo(1);
        assertThat(processedEvent.aiDescription()).isEqualTo("all good");
    }

    @Test
    @DisplayName("Throws MessageProcessingException when payload cannot be parsed")
    void consumeThrowsWhenParsingFails() throws Exception {
        String payload = "payload";
        when(objectMapper.readValue(payload, MessageUploadEventDto.class))
                .thenThrow(new JsonProcessingException("broken payload") {});

        assertThatThrownBy(() -> kafkaConsumer1.consume(payload))
                .isInstanceOf(MessageProcessingException.class)
                .hasMessageContaining("Failed to parse inbound message payload");

        verify(messageQueueProducer, never()).produce(eq(PRODUCER_TOPIC), any());
        verify(objectStorageService, never()).downloadFile(any());
        verify(llmService, never()).getSlotInfo(any(), any(), any());
    }
}

