package com.SmartParkingSystem.datafetch_service.dtos;

import com.SmartParkingSystem.datafetch_service.entities.ParkingLot;
import org.springframework.data.mongodb.core.geo.GeoJsonPoint;

import java.util.List;
import java.util.stream.Collectors;

public record LatLng(double latitude, double longitude) {

    public static List<LatLng> toLatLngList(List<ParkingLot> parkingLots) {
        return parkingLots.stream()
                .map(lot -> {
                    GeoJsonPoint point = lot.getPosition();
                    return new LatLng(point.getY(), point.getX()); // (lat, lng)
                })
                .collect(Collectors.toList());
    }
}