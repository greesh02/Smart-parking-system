package com.SmartParkingSystem.datafetch_service.entities;

// ======================== COUNT ================================
public class Count {

    private int car;
    private int motorbike;
    private int bus;

    public Count() {}

    private Count(Builder builder) {
        this.car = builder.car;
        this.motorbike = builder.motorbike;
        this.bus = builder.bus;
    }

    public static class Builder {
        private int car;
        private int motorbike;
        private int bus;

        public Builder car(int car) {
            this.car = car;
            return this;
        }

        public Builder motorbike(int bike) {
            this.motorbike = bike;
            return this;
        }

        public Builder bus(int bus) {
            this.bus = bus;
            return this;
        }

        public Count build() {
            return new Count(this);
        }
    }

    public int getCar() { return car; }
    public void setCar(int car) { this.car = car; }

    public int getMotorbike() { return motorbike; }
    public void setMotorbike(int bike) { this.motorbike = bike; }

    public int getBus() { return bus; }
    public void setBus(int bus) { this.bus = bus; }
}


