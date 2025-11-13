package com.SmartParkingSystem.datafetch_service.adapters.MapsApi.DistanceMatrix;

import com.SmartParkingSystem.datafetch_service.dtos.DistanceMatrixRequestDto;
import com.SmartParkingSystem.datafetch_service.dtos.DistanceMatrixResponseDto;

public interface DistanceMatrixApi {
    DistanceMatrixResponseDto getDistanceMatrix(DistanceMatrixRequestDto distanceMatrixRequestDto);
}
