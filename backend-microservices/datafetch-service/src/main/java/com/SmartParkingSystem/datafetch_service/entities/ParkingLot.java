package com.SmartParkingSystem.datafetch_service.entities;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.geo.GeoJsonPoint;
import org.springframework.data.mongodb.core.mapping.Document;

@Document("${mongo.collection1}")
public class ParkingLot {


    private String id;
    @Id
    private String lotId;
    private GeoJsonPoint position;

    private String streetAddress;
    private String landmark;
    private String city;
    private String state;
    private String postalCode;
    private String country;

    private String imageUrlOriginal;
    private String imageUrlProcessed;

    private SlotInfo slotInfo;
    private LastUpdated lastUpdated;
    private String aiDescription;

    public ParkingLot() {}

    private ParkingLot(Builder builder) {
        this.id = builder.id;
        this.lotId = builder.lotId;
        this.position = builder.position;
        this.streetAddress = builder.streetAddress;
        this.landmark = builder.landmark;
        this.city = builder.city;
        this.state = builder.state;
        this.postalCode = builder.postalCode;
        this.country = builder.country;
        this.imageUrlOriginal = builder.imageUrlOriginal;
        this.imageUrlProcessed = builder.imageUrlProcessed;
        this.slotInfo = builder.slotInfo;
        this.lastUpdated = builder.lastUpdated;
        this.aiDescription = builder.aiDescription;
    }

    // -------- Builder ----------
    public static class Builder {
        private String id;
        private String lotId;
        private GeoJsonPoint position;
        private String streetAddress;
        private String landmark;
        private String city;
        private String state;
        private String postalCode;
        private String country;
        private String imageUrlOriginal;
        private String imageUrlProcessed;
        private SlotInfo slotInfo;
        private LastUpdated lastUpdated;
        private String aiDescription;

        public Builder id(String id) {
            this.id = id;
            return this;
        }

        public Builder lotId(String lotId) {
            this.lotId = lotId;
            return this;
        }

        public Builder position(GeoJsonPoint position) {
            this.position = position;
            return this;
        }

        public Builder streetAddress(String streetAddress) {
            this.streetAddress = streetAddress;
            return this;
        }

        public Builder landmark(String landmark) {
            this.landmark = landmark;
            return this;
        }

        public Builder city(String city) {
            this.city = city;
            return this;
        }

        public Builder state(String state) {
            this.state = state;
            return this;
        }

        public Builder postalCode(String postalCode) {
            this.postalCode = postalCode;
            return this;
        }

        public Builder country(String country) {
            this.country = country;
            return this;
        }

        public Builder imageUrl(String imageUrlOriginal) {
            this.imageUrlOriginal = imageUrlOriginal;
            return this;
        }

        public Builder processedImageUrl(String imageUrlProcessed) {
            this.imageUrlProcessed = imageUrlProcessed;
            return this;
        }

        public Builder slotInfo(SlotInfo slotInfo) {
            this.slotInfo = slotInfo;
            return this;
        }

        public Builder lastUpdated(LastUpdated lastUpdated) {
            this.lastUpdated = lastUpdated;
            return this;
        }
        public Builder aiDescription(String aiDescription) {
            this.aiDescription = aiDescription;
            return this;
        }

        public ParkingLot build() {
            return new ParkingLot(this);
        }
    }

    // Getters and setters (unchanged)
    // ----------------------------------------------------------

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getLotId() { return lotId; }
    public void setLotId(String lotId) { this.lotId = lotId; }

    public GeoJsonPoint getPosition() { return position; }
    public void setPosition(GeoJsonPoint position) { this.position = position; }

    public String getStreetAddress() { return streetAddress; }
    public void setStreetAddress(String streetAddress) { this.streetAddress = streetAddress; }

    public String getLandmark() { return landmark; }
    public void setLandmark(String landmark) { this.landmark = landmark; }

    public String getCity() { return city; }
    public void setCity(String city) { this.city = city; }

    public String getState() { return state; }
    public void setState(String state) { this.state = state; }

    public String getPostalCode() { return postalCode; }
    public void setPostalCode(String postalCode) { this.postalCode = postalCode; }

    public String getCountry() { return country; }
    public void setCountry(String country) { this.country = country; }


    public SlotInfo getSlotInfo() { return slotInfo; }
    public void setSlotInfo(SlotInfo slotInfo) { this.slotInfo = slotInfo; }

    public LastUpdated getLastUpdated() { return lastUpdated; }
    public void setLastUpdated(LastUpdated lastUpdated) { this.lastUpdated = lastUpdated; }

    public String getAiDescription() {
        return aiDescription;
    }

    public String getImageUrlOriginal() {
        return imageUrlOriginal;
    }

    public void setImageUrlOriginal(String imageUrlOriginal) {
        this.imageUrlOriginal = imageUrlOriginal;
    }

    public String getImageUrlProcessed() {
        return imageUrlProcessed;
    }

    public void setImageUrlProcessed(String imageUrlProcessed) {
        this.imageUrlProcessed = imageUrlProcessed;
    }

    public void setAiDescription(String aiDescription) {
        this.aiDescription = aiDescription;
    }
}






