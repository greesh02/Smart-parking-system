package com.db_writes_service.dtos;

import com.fasterxml.jackson.annotation.JsonProperty;


public record SlotCountInfo(
        @JsonProperty(value = "car") int car,
        @JsonProperty(value = "motorbike") int motorBike,
        @JsonProperty(value = "bus") int bus
) {

}