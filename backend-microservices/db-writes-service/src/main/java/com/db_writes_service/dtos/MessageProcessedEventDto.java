package com.db_writes_service.dtos;


import com.fasterxml.jackson.annotation.JsonProperty;

public record MessageProcessedEventDto(
        @JsonProperty(required = true, value = "event") String event,
        @JsonProperty(required = true, value = "lotId") String lotId,
        @JsonProperty(required = true, value = "imageUrlOriginal") String imageUrlOriginal,
        @JsonProperty(required = true, value = "imageUrlProcessed") String imageUrlProcessed,
        @JsonProperty(required = true, value = "slotInfo") SlotInfo slotInfo,
        @JsonProperty(required = true,value = "aiDescription") String aiDescription,
        @JsonProperty(required = true, value = "lastUpdated") LastUpdated lastUpdated
) {


    public record SlotInfo(
            @JsonProperty(required = true, value = "occupiedSlotsCount")
            SlotCountInfo occupiedSlotsCount,
            @JsonProperty(required = true, value = "availableSlotsCount")
            SlotCountInfo availableSlotsCount
    ) {

    }


    public record LastUpdated(
            @JsonProperty(required = true, value = "cameraService") String cameraService,
            @JsonProperty(required = true, value = "aiService") String aiService
    ) {


    }
}
