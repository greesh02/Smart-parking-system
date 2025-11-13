package com.SmartParking.ai_service.dtos;

import com.fasterxml.jackson.annotation.JsonProperty;

public record LlmSlotInfoResponseDto(
                                     @JsonProperty(value = "car") int car,
                                     @JsonProperty(value = "motorbike") int motorBike,
                                     @JsonProperty(value = "bus") int bus,
                                     @JsonProperty(value = "description") String description) {

}
