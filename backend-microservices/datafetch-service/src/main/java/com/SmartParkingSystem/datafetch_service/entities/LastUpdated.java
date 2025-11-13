package com.SmartParkingSystem.datafetch_service.entities;
// ===================== LAST UPDATED ============================
public class LastUpdated {

    private String cameraService;
    private String aiService;

    public LastUpdated() {}

    private LastUpdated(Builder builder) {
        this.cameraService = builder.cameraService;
        this.aiService = builder.aiService;
    }

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
            return new LastUpdated(this);
        }
    }

    public String getCameraService() { return cameraService; }
    public void setCameraService(String cameraService) { this.cameraService = cameraService; }

    public String getAiService() { return aiService; }
    public void setAiService(String aiService) { this.aiService = aiService; }
}
