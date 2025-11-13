package com.SmartParkingSystem.datafetch_service.dtos;

import java.util.List;


public record DistanceMatrixRequestDto(
        LatLng source,
        List<LatLng> destinations,
        String travelMode
) {




}