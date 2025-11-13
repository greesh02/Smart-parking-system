package com.SmartParkingSystem.datafetch_service.adapters.MapsApi.GeoCode;

import java.util.List;

public record GoogleMapsGeoCodeResponseDto(List<GeocodeResult> results){
    public record GeocodeResult(GeocodeGeometry geometry) {
        public record GeocodeGeometry(GeocodeLocation location){
            public record GeocodeLocation(double lat,double lng) {
            }
        }
    }
}


