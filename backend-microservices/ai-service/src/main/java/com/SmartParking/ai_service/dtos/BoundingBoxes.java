package com.SmartParking.ai_service.dtos;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

public record BoundingBoxes(@JsonProperty(required = true, value = "occupiedSlots") List<OccupiedSlot> occupiedSlots){
    public record OccupiedSlot(@JsonProperty(required = true, value = "box") List<Integer> box,
                               @JsonProperty(required = true, value = "class") String className,
                               @JsonProperty(required = true, value = "confidence") float confidence){

    }
}