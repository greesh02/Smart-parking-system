package com.SmartParkingSystem.datafetch_service.enums;

public enum VehicleType {
    CAR("car"),
    MOTORBIKE("motorbike"),
    BUS("bus");

    private final String value;

    VehicleType(String value) {
        this.value = value;
    }

    @Override
    public String toString() {
        return this.value;
    }

    public String getValue() {
        return value;
    }

    public TravelMode getTravelMode(){
        if(this.equals(VehicleType.MOTORBIKE)){
            return TravelMode.TWO_WHEELER;
        }
        else{
            return TravelMode.DRIVE;
        }
    }
}