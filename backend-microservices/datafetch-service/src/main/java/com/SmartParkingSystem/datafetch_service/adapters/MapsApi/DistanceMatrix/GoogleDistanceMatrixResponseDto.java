package com.SmartParkingSystem.datafetch_service.adapters.MapsApi.DistanceMatrix;


import com.SmartParkingSystem.datafetch_service.enums.RouteMatrixCondition;

public record GoogleDistanceMatrixResponseDto(
        int originIndex,
        int destinationIndex,
        int distanceMeters,
        String duration,
        RouteMatrixCondition condition
) {


}

