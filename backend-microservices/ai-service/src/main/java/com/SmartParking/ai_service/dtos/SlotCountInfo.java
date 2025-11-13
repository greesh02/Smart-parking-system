package com.SmartParking.ai_service.dtos;

import com.fasterxml.jackson.annotation.JsonProperty;


public record SlotCountInfo(
        @JsonProperty(value = "car") int car,
        @JsonProperty(value = "motorbike") int motorBike,
        @JsonProperty(value = "bus") int bus
) {
    public static class Builder {
        private int car;
        private int motorBike;
        private int bus;

        public Builder car(int car) {
            this.car = car;
            return this;
        }

        public Builder motorBike(int motorBike) {
            this.motorBike = motorBike;
            return this;
        }

        public Builder bus(int bus) {
            this.bus = bus;
            return this;
        }

        public SlotCountInfo build() {
            return new SlotCountInfo(car, motorBike, bus);
        }
    }

    public static Builder builder() {
        return new Builder();
    }
}