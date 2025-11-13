package com.db_writes_service.entities;

// ======================= SLOT INFO ============================
public class SlotInfo {

    private Count availableSlotsCount;
    private Count occupiedSlotsCount;

    public SlotInfo() {}

    private SlotInfo(Builder builder) {
        this.availableSlotsCount = builder.availableSlotsCount;
        this.occupiedSlotsCount = builder.occupiedSlotsCount;
    }

    public static class Builder {
        private Count availableSlotsCount;
        private Count occupiedSlotsCount;

        public Builder availableSlotsCount(Count availableSlotsCount) {
            this.availableSlotsCount = availableSlotsCount;
            return this;
        }

        public Builder occupiedSlotsCount(Count occupiedSlotsCount) {
            this.occupiedSlotsCount = occupiedSlotsCount;
            return this;
        }

        public SlotInfo build() {
            return new SlotInfo(this);
        }
    }

    public Count getAvailableSlotsCount() { return availableSlotsCount; }
    public void setAvailableSlotsCount(Count availableSlotsCount) { this.availableSlotsCount = availableSlotsCount; }

    public Count getOccupiedSlotsCount() { return occupiedSlotsCount; }
    public void setOccupiedSlotsCount(Count occupiedSlotsCount) { this.occupiedSlotsCount = occupiedSlotsCount; }

}