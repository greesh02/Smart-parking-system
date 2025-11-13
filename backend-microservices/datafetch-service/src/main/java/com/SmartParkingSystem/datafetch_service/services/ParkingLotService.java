package com.SmartParkingSystem.datafetch_service.services;

import com.SmartParkingSystem.datafetch_service.dtos.FindParkingLotResponseDto;
import com.SmartParkingSystem.datafetch_service.dtos.ParkingLotServiceResponseDto;
import com.SmartParkingSystem.datafetch_service.dtos.ParkingLotServiceResponseItem;
import com.SmartParkingSystem.datafetch_service.entities.ParkingLot;

import java.util.List;

public interface ParkingLotService {
    ParkingLotServiceResponseDto findByGeo(double lat, double lng, double radius, String vehicleType);
    ParkingLotServiceResponseDto findByCity(String address,double radius,String vehicleType);
}
