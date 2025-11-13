package com.SmartParkingSystem.datafetch_service.controllers;

import com.SmartParkingSystem.datafetch_service.dtos.DistanceMatrixResponseItem;
import com.SmartParkingSystem.datafetch_service.dtos.LatLng;
import com.SmartParkingSystem.datafetch_service.dtos.ParkingLotServiceResponseDto;
import com.SmartParkingSystem.datafetch_service.dtos.ParkingLotServiceResponseItem;
import com.SmartParkingSystem.datafetch_service.entities.ParkingLot;
import com.SmartParkingSystem.datafetch_service.enums.RouteMatrixCondition;
import com.SmartParkingSystem.datafetch_service.exceptions.ExternalServiceException;
import com.SmartParkingSystem.datafetch_service.exceptions.GlobalExceptionHandler;
import com.SmartParkingSystem.datafetch_service.services.ParkingLotService;
import com.SmartParkingSystem.datafetch_service.services.objectStorageServices.ObjectStorageService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;

import org.springframework.context.annotation.Import;
import org.springframework.data.mongodb.core.geo.GeoJsonPoint;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(ParkingLotController.class)
@Import(GlobalExceptionHandler.class)
class ParkingLotControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ParkingLotService parkingLotService;

    @MockBean
    private ObjectStorageService objectStorageService;

    @Test
    @DisplayName("Should return parking lots when latitude and longitude provided")
    void shouldReturnParkingLotsByGeo() throws Exception {
        ParkingLot parkingLot = new ParkingLot();
        parkingLot.setLotId("LOT-1");
        parkingLot.setPosition(new GeoJsonPoint(77.0, 12.9));

        DistanceMatrixResponseItem responseItem = new DistanceMatrixResponseItem(
                0,
                0,
                1200,
                "10 mins",
                RouteMatrixCondition.ROUTE_EXISTS
        );
        ParkingLotServiceResponseItem parkingLotServiceResponseItem =
                new ParkingLotServiceResponseItem(parkingLot, responseItem);
        ParkingLotServiceResponseDto serviceResponse = new ParkingLotServiceResponseDto(
                List.of(parkingLotServiceResponseItem),
                new LatLng(12.9, 77.0)
        );

        when(parkingLotService.findByGeo(12.9, 77.0, 1.5, "CAR")).thenReturn(serviceResponse);

        mockMvc.perform(get("/api/v1/parkingLot")
                        .param("lat", "12.9")
                        .param("long", "77.0")
                        .param("radius", "1.5")
                        .param("vehicleType", "CAR"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.parkingLots[0].parkingLot.lotId").value("LOT-1"))
                .andExpect(jsonPath("$.count").value(1))
                .andExpect(jsonPath("$.coordinates.latitude").value(12.9));
    }

    @Test
    @DisplayName("Should return parking lots when address provided")
    void shouldReturnParkingLotsByAddress() throws Exception {
        ParkingLot parkingLot = new ParkingLot();
        parkingLot.setLotId("LOT-2");
        parkingLot.setPosition(new GeoJsonPoint(78.0, 13.0));

        DistanceMatrixResponseItem responseItem = new DistanceMatrixResponseItem(
                0,
                0,
                900,
                "8 mins",
                RouteMatrixCondition.ROUTE_EXISTS
        );
        ParkingLotServiceResponseItem parkingLotServiceResponseItem =
                new ParkingLotServiceResponseItem(parkingLot, responseItem);
        ParkingLotServiceResponseDto serviceResponse = new ParkingLotServiceResponseDto(
                List.of(parkingLotServiceResponseItem),
                new LatLng(13.0, 78.0)
        );

        when(parkingLotService.findByCity("Test City", 2.0, "CAR")).thenReturn(serviceResponse);

        mockMvc.perform(get("/api/v1/parkingLot")
                        .param("address", "Test City")
                        .param("radius", "2.0")
                        .param("vehicleType", "CAR"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.parkingLots[0].parkingLot.lotId").value("LOT-2"))
                .andExpect(jsonPath("$.coordinates.latitude").value(13.0));
    }

    @Test
    @DisplayName("Should return bad request when required params missing")
    void shouldReturnBadRequestWhenParamsMissing() throws Exception {
        mockMvc.perform(get("/api/v1/parkingLot")
                        .param("radius", "1.0")
                        .param("vehicleType", "CAR"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.message").value("Either (lat,long,radius,vehicleType) OR (city,radius,vehicleType) must be provided."));
    }

    @Test
    @DisplayName("Should translate service exception to 502")
    void shouldTranslateToBadGatewayOnServiceFailure() throws Exception {
        when(parkingLotService.findByGeo(12.9, 77.0, 1.5, "CAR"))
                .thenThrow(new ExternalServiceException("Service unavailable"));

        mockMvc.perform(get("/api/v1/parkingLot")
                        .param("lat", "12.9")
                        .param("long", "77.0")
                        .param("radius", "1.5")
                        .param("vehicleType", "CAR"))
                .andExpect(status().isBadGateway())
                .andExpect(jsonPath("$.status").value(502))
                .andExpect(jsonPath("$.message").value("Service unavailable"));
    }
}
