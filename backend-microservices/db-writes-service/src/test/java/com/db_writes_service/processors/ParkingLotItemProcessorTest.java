package com.db_writes_service.processors;

import com.db_writes_service.dtos.MessageProcessedEventDto;
import com.db_writes_service.dtos.SlotCountInfo;
import com.db_writes_service.entities.ParkingLot;
import com.db_writes_service.processors.mappers.ParkingLotMapperImpl;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class ParkingLotItemProcessorTest {

    @Test
    @DisplayName("ParkingLotItemProcessor maps incoming JSON payload to ParkingLot entity")
    void process_parsesJsonAndMapsToEntity() throws Exception {
        ObjectMapper objectMapper = new ObjectMapper();
        ParkingLotItemProcessor processor = new ParkingLotItemProcessor(objectMapper, new ParkingLotMapperImpl());

        String json = """
                {
                  "lotId": "LOT-123",
                  "imageUrlOriginal": "s3://bucket/original.jpg",
                  "imageUrlProcessed": "s3://bucket/processed.jpg",
                  "slotInfo": {
                    "availableSlotsCount": { "motorBike": 1, "car": 2, "bus": 0 },
                    "occupiedSlotsCount": { "motorBike": 0, "car": 1, "bus": 0 }
                  },
                  "lastUpdated": { "cameraService": "2025-11-13T10:00:00Z", "aiService": "2025-11-13T10:01:00Z" },
                  "aiDescription": "ok"
                }
                """;

        ParkingLot lot = processor.process(json);
        assertThat(lot.getLotId()).isEqualTo("LOT-123");
        assertThat(lot.getImageUrlOriginal()).isEqualTo("s3://bucket/original.jpg");
        assertThat(lot.getSlotInfo()).isNotNull();
        assertThat(lot.getSlotInfo().getAvailableSlotsCount().getCar()).isEqualTo(2);
    }
}


