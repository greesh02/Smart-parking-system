package com.SmartParkingSystem.datafetch_service.dtos;

import com.SmartParkingSystem.datafetch_service.enums.RouteMatrixCondition;

import java.util.List;

public record DistanceMatrixResponseDto(List<DistanceMatrixResponseItem> distanceMatrixResponseItems) {


}

