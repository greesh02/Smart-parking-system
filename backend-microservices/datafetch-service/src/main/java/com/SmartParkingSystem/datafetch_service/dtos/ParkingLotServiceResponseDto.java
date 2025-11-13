package com.SmartParkingSystem.datafetch_service.dtos;

import java.util.List;

public record ParkingLotServiceResponseDto(List<ParkingLotServiceResponseItem> parkingLotServiceResponseItems,LatLng source) {
}
