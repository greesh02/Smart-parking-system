package com.SmartParkingSystem.datafetch_service.adapters.MapsApi.GeoCode;

import com.SmartParkingSystem.datafetch_service.dtos.GeoCodeRequestDto;
import com.SmartParkingSystem.datafetch_service.dtos.GeoCodeResponseDto;

public interface GeoCodeApiAdapter {

    GeoCodeResponseDto geocode(GeoCodeRequestDto geoCodeRequestDto);

}
