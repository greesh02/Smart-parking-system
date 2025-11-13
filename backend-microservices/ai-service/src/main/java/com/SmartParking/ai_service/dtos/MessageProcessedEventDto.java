package com.SmartParking.ai_service.dtos;


import com.fasterxml.jackson.annotation.JsonProperty;


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

    // ----------------------------------------------------------------------------------------
    // Top-level Builder
    // ----------------------------------------------------------------------------------------
    public static class Builder {
        private String event;
        private String lotId;
        private String imageUrlOriginal;
        private String imageUrlProcessed;
        private SlotInfo slotInfo;
        private String aiDescription;
        private LastUpdated lastUpdated;

        public Builder event(String event) {
            this.event = event;
            return this;
        }

        public Builder lotId(String lotId) {
            this.lotId = lotId;
            return this;
        }

        public Builder imageUrlOriginal(String imageUrlOriginal) {
            this.imageUrlOriginal = imageUrlOriginal;
            return this;
        }

        public Builder imageUrlProcessed(String imageUrlProcessed) {
            this.imageUrlProcessed = imageUrlProcessed;
            return this;
        }

        public Builder slotInfo(SlotInfo slotInfo) {
            this.slotInfo = slotInfo;
            return this;
        }

        public Builder aiDescription(String aiDescription) {
            this.aiDescription = aiDescription;
            return this;
        }

        public Builder lastUpdated(LastUpdated lastUpdated) {
            this.lastUpdated = lastUpdated;
            return this;
        }

        public MessageProcessedEventDto build() {
            return new MessageProcessedEventDto(
                    event,
                    lotId,
                    imageUrlOriginal,
                    imageUrlProcessed,
                    slotInfo,
                    aiDescription,
                    lastUpdated
            );
        }
    }

    public static Builder builder() {
        return new Builder();
    }

    // ----------------------------------------------------------------------------------------
    //  SlotInfo Builder (with both nested count objects)
    // ----------------------------------------------------------------------------------------

    public record SlotInfo(
            @JsonProperty(required = true, value = "occupiedSlotsCount")
            SlotCountInfo occupiedSlotsCount,
            @JsonProperty(required = true, value = "availableSlotsCount")
            SlotCountInfo availableSlotsCount
    ) {

        public static class Builder {
            private SlotCountInfo occupiedSlotsCount;
            private SlotCountInfo availableSlotsCount;

            public Builder occupiedSlotsCount(SlotCountInfo occupiedSlotsCount) {
                this.occupiedSlotsCount = occupiedSlotsCount;
                return this;
            }

            public Builder availableSlotsCount(SlotCountInfo availableSlotsCount) {
                this.availableSlotsCount = availableSlotsCount;
                return this;
            }

            public SlotInfo build() {
                return new SlotInfo(occupiedSlotsCount, availableSlotsCount);
            }
        }

        public static Builder builder() {
            return new Builder();
        }



    }

    // ----------------------------------------------------------------------------------------
    // LastUpdated Builder
    // ----------------------------------------------------------------------------------------

    public record LastUpdated(
            @JsonProperty(required = true, value = "cameraService") String cameraService,
            @JsonProperty(required = true, value = "aiService") String aiService
    ) {

        public static class Builder {
            private String cameraService;
            private String aiService;

            public Builder cameraService(String cameraService) {
                this.cameraService = cameraService;
                return this;
            }

            public Builder aiService(String aiService) {
                this.aiService = aiService;
                return this;
            }

            public LastUpdated build() {
                return new LastUpdated(cameraService, aiService);
            }
        }

        public static Builder builder() {
            return new Builder();
        }
    }
}
