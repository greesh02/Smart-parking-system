package com.SmartParkingSystem.datafetch_service.dtos;

import com.SmartParkingSystem.datafetch_service.entities.ParkingLot;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

public record FindParkingLotResponseDto(@JsonProperty(required = true, value = "parkingLots") List<ParkingLotServiceResponseItem> parkingLots,
                                        @JsonProperty(required = true, value = "count") int count,
                                        @JsonProperty(required = true, value = "coordinates") LatLng coordinates){
}
