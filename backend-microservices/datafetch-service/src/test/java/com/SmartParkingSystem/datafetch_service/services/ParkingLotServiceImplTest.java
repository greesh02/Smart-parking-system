package com.SmartParkingSystem.datafetch_service.services;

import com.SmartParkingSystem.datafetch_service.adapters.MapsApi.DistanceMatrix.DistanceMatrixApi;
import com.SmartParkingSystem.datafetch_service.adapters.MapsApi.GeoCode.GeoCodeApiAdapter;
import com.SmartParkingSystem.datafetch_service.dtos.DistanceMatrixRequestDto;
import com.SmartParkingSystem.datafetch_service.dtos.DistanceMatrixResponseDto;
import com.SmartParkingSystem.datafetch_service.dtos.DistanceMatrixResponseItem;
import com.SmartParkingSystem.datafetch_service.dtos.GeoCodeRequestDto;
import com.SmartParkingSystem.datafetch_service.dtos.GeoCodeResponseDto;
import com.SmartParkingSystem.datafetch_service.dtos.ParkingLotServiceResponseDto;
import com.SmartParkingSystem.datafetch_service.dtos.ParkingLotServiceResponseItem;
import com.SmartParkingSystem.datafetch_service.entities.ParkingLot;
import com.SmartParkingSystem.datafetch_service.enums.RouteMatrixCondition;
import com.SmartParkingSystem.datafetch_service.exceptions.ExternalServiceException;
import com.SmartParkingSystem.datafetch_service.exceptions.InvalidRequestException;
import com.SmartParkingSystem.datafetch_service.repositories.ParkingLotRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.core.geo.GeoJsonPoint;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyDouble;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ParkingLotServiceImplTest {

    @Mock
    private GeoCodeApiAdapter geoCodeApiAdapter;

    @Mock
    private DistanceMatrixApi distanceMatrixApi;

    @Mock
    private ParkingLotRepository parkingLotRepository;

    @InjectMocks
    private ParkingLotServiceImpl parkingLotService;

    @Test
    @DisplayName("findByGeo returns response items when repository and distance matrix succeed")
    void findByGeoReturnsResults() {
        ParkingLot parkingLot = new ParkingLot();
        parkingLot.setLotId("LOT-10");
        parkingLot.setPosition(new GeoJsonPoint(77.0, 12.9));

        when(parkingLotRepository.findAvailableSlotsWithinRadius(
                anyDouble(),
                anyDouble(),
                anyDouble(),
                anyString(),
                any(Pageable.class)
        )).thenReturn(List.of(parkingLot));

        DistanceMatrixResponseItem responseItem = new DistanceMatrixResponseItem(
                0,
                0,
                800,
                "7 mins",
                RouteMatrixCondition.ROUTE_EXISTS
        );
        DistanceMatrixResponseDto responseDto = new DistanceMatrixResponseDto(List.of(responseItem));
        when(distanceMatrixApi.getDistanceMatrix(any(DistanceMatrixRequestDto.class))).thenReturn(responseDto);

        ParkingLotServiceResponseDto result = parkingLotService.findByGeo(12.9, 77.0, 2.0, "CAR");

        assertThat(result.parkingLotServiceResponseItems()).hasSize(1);
        ParkingLotServiceResponseItem item = result.parkingLotServiceResponseItems().get(0);
        assertThat(item.parkingLot().getLotId()).isEqualTo("LOT-10");
        verify(distanceMatrixApi).getDistanceMatrix(any(DistanceMatrixRequestDto.class));
    }

    @Test
    @DisplayName("findByGeo throws InvalidRequestException on unsupported vehicle type")
    void findByGeoThrowsOnInvalidVehicleType() {
        assertThatThrownBy(() -> parkingLotService.findByGeo(12.9, 77.0, 2.0, "PLANE"))
                .isInstanceOf(InvalidRequestException.class)
                .hasMessageContaining("Unsupported vehicle type");
    }

    @Test
    @DisplayName("findByGeo wraps repository exception as ExternalServiceException")
    void findByGeoWrapsRepositoryException() {
        when(parkingLotRepository.findAvailableSlotsWithinRadius(
                anyDouble(),
                anyDouble(),
                anyDouble(),
                anyString(),
                any(Pageable.class)
        )).thenThrow(new RuntimeException("db down"));

        assertThatThrownBy(() -> parkingLotService.findByGeo(12.9, 77.0, 2.0, "CAR"))
                .isInstanceOf(ExternalServiceException.class)
                .hasMessageContaining("Failed to fetch parking lot data from repository");
    }

    @Test
    @DisplayName("findByCity delegates to geo service and repository")
    void findByCityDelegatesToGeoService() {
        when(geoCodeApiAdapter.geocode(any(GeoCodeRequestDto.class)))
                .thenReturn(new GeoCodeResponseDto(12.9, 77.0));

        when(parkingLotRepository.findAvailableSlotsWithinRadius(
                anyDouble(),
                anyDouble(),
                anyDouble(),
                anyString(),
                any(Pageable.class)
        )).thenReturn(List.of());

        ParkingLotServiceResponseDto result = parkingLotService.findByCity("Test City", 2.0, "CAR");

        assertThat(result.parkingLotServiceResponseItems()).isEmpty();
        verify(geoCodeApiAdapter).geocode(any(GeoCodeRequestDto.class));
        verify(parkingLotRepository).findAvailableSlotsWithinRadius(
                eq(77.0),
                eq(12.9),
                anyDouble(),
                anyString(),
                any(Pageable.class)
        );
        verify(distanceMatrixApi, never()).getDistanceMatrix(any(DistanceMatrixRequestDto.class));
    }
}


