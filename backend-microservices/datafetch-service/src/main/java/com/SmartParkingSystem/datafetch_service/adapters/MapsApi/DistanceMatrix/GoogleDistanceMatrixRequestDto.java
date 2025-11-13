package com.SmartParkingSystem.datafetch_service.adapters.MapsApi.DistanceMatrix;


import com.SmartParkingSystem.datafetch_service.dtos.OriginOrDestination;
import com.SmartParkingSystem.datafetch_service.dtos.WayPoint;

import java.util.List;

public record GoogleDistanceMatrixRequestDto(
        List<OriginOrDestination> origins,
        List<OriginOrDestination> destinations,
        String travelMode,
        String routingPreference
) {




}