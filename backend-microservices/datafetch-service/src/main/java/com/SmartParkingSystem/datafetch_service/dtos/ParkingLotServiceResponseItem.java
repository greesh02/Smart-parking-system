package com.SmartParkingSystem.datafetch_service.dtos;

import com.SmartParkingSystem.datafetch_service.entities.ParkingLot;

public record ParkingLotServiceResponseItem(ParkingLot parkingLot,DistanceMatrixResponseItem distanceMatrixResponseItem) {
}
