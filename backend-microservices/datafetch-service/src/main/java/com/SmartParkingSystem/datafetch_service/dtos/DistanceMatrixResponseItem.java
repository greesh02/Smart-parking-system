package com.SmartParkingSystem.datafetch_service.dtos;

import com.SmartParkingSystem.datafetch_service.enums.RouteMatrixCondition;

public record DistanceMatrixResponseItem(
        int originIndex,
        int destinationIndex,
        int distanceMeters,
        String duration,
        RouteMatrixCondition condition
) {


}