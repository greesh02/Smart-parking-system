package com.SmartParking.ai_service.dtos;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

public record MessageUploadEventDto(@JsonProperty(required = true, value = "event") String event,
                                    @JsonProperty(required = true, value = "lotId") String lotId,
                                    @JsonProperty(required = true, value = "imageUrlOriginal") String imageUrlOriginal,
                                    @JsonProperty(required = true, value = "imageUrlProcessed") String imageUrlProcessed,
                                    @JsonProperty(required = true, value = "slotInfo") SlotInfo slotInfo,
                                    @JsonProperty(required = true, value = "boundingBoxes") BoundingBoxes boundingBoxes,
                                    @JsonProperty(required = true, value = "lastUpdated") LastUpdated lastUpdated
                                    ) {

    public record SlotInfo(@JsonProperty(required = true, value = "occupiedSlotsCount") SlotCountInfo occupiedSlotsCount){

    }



    public record LastUpdated(@JsonProperty(required = true, value = "cameraService") String cameraService){

    }
}

