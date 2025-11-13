package com.SmartParkingSystem.datafetch_service.services;


import com.SmartParkingSystem.datafetch_service.adapters.MapsApi.DistanceMatrix.DistanceMatrixApi;
import com.SmartParkingSystem.datafetch_service.adapters.MapsApi.GeoCode.GeoCodeApiAdapter;
import com.SmartParkingSystem.datafetch_service.dtos.*;
import com.SmartParkingSystem.datafetch_service.entities.ParkingLot;
import com.SmartParkingSystem.datafetch_service.enums.RouteMatrixCondition;
import com.SmartParkingSystem.datafetch_service.enums.VehicleType;
import com.SmartParkingSystem.datafetch_service.exceptions.ExternalServiceException;
import com.SmartParkingSystem.datafetch_service.exceptions.InvalidRequestException;
import com.SmartParkingSystem.datafetch_service.repositories.ParkingLotRepository;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class ParkingLotServiceImpl implements ParkingLotService {
    private static final Logger log = LogManager.getLogger(ParkingLotServiceImpl.class);
    private final GeoCodeApiAdapter geoCodeApiAdapter;
    private final DistanceMatrixApi distanceMatrixApi;
    private final ParkingLotRepository parkingLotRepository;



    public ParkingLotServiceImpl(GeoCodeApiAdapter geoCodeApiAdapter, DistanceMatrixApi distanceMatrixApi, ParkingLotRepository parkingLotRepository) {
        this.geoCodeApiAdapter = geoCodeApiAdapter;
        this.distanceMatrixApi = distanceMatrixApi;
        this.parkingLotRepository = parkingLotRepository;
    }

    @Override
    public ParkingLotServiceResponseDto findByGeo(double lat, double lng, double radius, String vehicleType) {
        log.info("Finding parking lots by geo coordinates. lat={}, lng={}, radius={}, vehicleType={}",
                lat, lng, radius, vehicleType);
        try {
            VehicleType resolvedVehicleType = resolveVehicleType(vehicleType);

            int limit = 200;
            Pageable pageable = PageRequest.of(0, limit);
            double radiusMeters = radius * 1000;
            double radiusInRadians = radiusMeters / 6378137.0;

            List<ParkingLot> parkingLots = fetchParkingLots(lng, lat, radiusInRadians, resolvedVehicleType, pageable);

            LatLng source = new LatLng(lat, lng);
            List<LatLng> destinations = LatLng.toLatLngList(parkingLots);

            if (destinations.isEmpty()) {
                log.info("No parking lots found within radius {} for vehicle type {}", radius, vehicleType);
                return new ParkingLotServiceResponseDto(new ArrayList<>(), new LatLng(0.0, 0.0));
            }

            DistanceMatrixRequestDto distanceMatrixRequestDto = new DistanceMatrixRequestDto(
                    source,
                    destinations,
                    resolvedVehicleType.getTravelMode().toString()
            );

            DistanceMatrixResponseDto distanceMatrixResponseDto = fetchDistanceMatrix(distanceMatrixRequestDto);

            List<ParkingLotServiceResponseItem> responseItems = new ArrayList<>();
            for (ParkingLot pk : parkingLots) {
                responseItems.add(null);
            }

            for (DistanceMatrixResponseItem dmrt : distanceMatrixResponseDto.distanceMatrixResponseItems()) {
                if (dmrt.condition().equals(RouteMatrixCondition.ROUTE_EXISTS)) {
                    responseItems.set(
                            dmrt.destinationIndex(),
                            new ParkingLotServiceResponseItem(parkingLots.get(dmrt.destinationIndex()), dmrt)
                    );
                }
            }

            List<ParkingLotServiceResponseItem> filteredResponseItems = new ArrayList<>();
            for (ParkingLotServiceResponseItem responseItem : responseItems) {
                if (responseItem != null) {
                    filteredResponseItems.add(responseItem);
                }
            }

            ParkingLotServiceResponseDto responseDto = new ParkingLotServiceResponseDto(filteredResponseItems, source);
            log.debug("Returning {} parking lots for geo search", filteredResponseItems.size());
            return responseDto;
        } catch (InvalidRequestException ex) {
            log.warn("Invalid findByGeo request: {}", ex.getMessage());
            throw ex;
        } catch (ExternalServiceException ex) {
            log.error("External service error during findByGeo: {}", ex.getMessage());
            throw ex;
        } catch (RuntimeException ex) {
            log.error("Unexpected error in findByGeo", ex);
            throw new ExternalServiceException("Failed to fetch parking lot data", ex);
        }
    }

    @Override
    public ParkingLotServiceResponseDto findByCity(String address, double radius, String vehicleType) {
        log.info("Finding parking lots by city. address={}, radius={}, vehicleType={}",
                address, radius, vehicleType);
        try {
            GeoCodeRequestDto geoCodeRequestDto = new GeoCodeRequestDto(address);
            GeoCodeResponseDto geoCodeResponseDto = geoCodeApiAdapter.geocode(geoCodeRequestDto);
            double lat = geoCodeResponseDto.lat();
            double lng = geoCodeResponseDto.lng();
            return findByGeo(lat, lng, radius, vehicleType);
        } catch (InvalidRequestException ex) {
            log.warn("Invalid findByCity request: {}", ex.getMessage());
            throw ex;
        } catch (ExternalServiceException ex) {
            log.error("External service error during findByCity: {}", ex.getMessage());
            throw ex;
        } catch (RuntimeException ex) {
            log.error("Unexpected error in findByCity", ex);
            throw new ExternalServiceException("Failed to process address lookup", ex);
        }
    }

    private VehicleType resolveVehicleType(String vehicleType) {
        if (vehicleType == null || vehicleType.isBlank()) {
            throw new InvalidRequestException("Vehicle type must be provided");
        }
        try {
            return VehicleType.valueOf(vehicleType.toUpperCase());
        } catch (IllegalArgumentException ex) {
            throw new InvalidRequestException("Unsupported vehicle type: " + vehicleType, ex);
        }
    }

    private List<ParkingLot> fetchParkingLots(
            double longitude,
            double latitude,
            double radiusInRadians,
            VehicleType vehicleType,
            Pageable pageable
    ) {
        try {
            return parkingLotRepository.findAvailableSlotsWithinRadius(
                    longitude,
                    latitude,
                    radiusInRadians,
                    vehicleType.getValue(),
                    pageable
            );
        } catch (RuntimeException ex) {
            throw new ExternalServiceException("Failed to fetch parking lot data from repository", ex);
        }
    }

    private DistanceMatrixResponseDto fetchDistanceMatrix(DistanceMatrixRequestDto requestDto) {
        try {
            return distanceMatrixApi.getDistanceMatrix(requestDto);
        } catch (RuntimeException ex) {
            throw new ExternalServiceException("Failed to retrieve distance matrix data", ex);
        }
    }


}
