package com.SmartParkingSystem.datafetch_service.controllers;

import com.SmartParkingSystem.datafetch_service.dtos.FindParkingLotResponseDto;
import com.SmartParkingSystem.datafetch_service.dtos.ParkingLotServiceResponseDto;
import com.SmartParkingSystem.datafetch_service.exceptions.ExternalServiceException;
import com.SmartParkingSystem.datafetch_service.exceptions.InvalidRequestException;
import com.SmartParkingSystem.datafetch_service.exceptions.ResourceNotFoundException;
import com.SmartParkingSystem.datafetch_service.exceptions.StorageException;
import com.SmartParkingSystem.datafetch_service.services.ParkingLotService;
import com.SmartParkingSystem.datafetch_service.services.objectStorageServices.ObjectStorageService;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/")
public class ParkingLotController {

    private static final Logger log = LogManager.getLogger(ParkingLotController.class);

    private final ParkingLotService parkingLotService;
    private final ObjectStorageService objectStorageService;

    public ParkingLotController(ParkingLotService parkingLotService, ObjectStorageService objectStorageService) {
        this.parkingLotService = parkingLotService;
        this.objectStorageService = objectStorageService;
    }

    @GetMapping("/parkingLot")
    public ResponseEntity<FindParkingLotResponseDto> findParkingLots(
            @RequestParam(value = "lat", required = false) Double lat,
            @RequestParam(required = false, value = "long") Double lng,
            @RequestParam(required = false, value = "address") String address,
            @RequestParam(value = "radius") double radius,
            @RequestParam(value = "vehicleType") String vehicleType
    ) {
        log.info("Received parking lot search request. lat={}, long={}, address={}, radius={}, vehicleType={}",
                lat, lng, address, radius, vehicleType);
        try {
            FindParkingLotResponseDto findParkingLotResponseDto;
            // CASE 1: lat/long search
            if (lat != null && lng != null) {
                ParkingLotServiceResponseDto result = parkingLotService.findByGeo(lat, lng, radius, vehicleType);
                findParkingLotResponseDto = new FindParkingLotResponseDto(
                        result.parkingLotServiceResponseItems(),
                        result.parkingLotServiceResponseItems().size(),
                        result.source());
                return ResponseEntity.ok(findParkingLotResponseDto);
            }

            // CASE 2: city search
            if (address != null) {
                ParkingLotServiceResponseDto result =
                        parkingLotService.findByCity(address, radius, vehicleType);
                findParkingLotResponseDto = new FindParkingLotResponseDto(
                        result.parkingLotServiceResponseItems(),
                        result.parkingLotServiceResponseItems().size(),
                        result.source());
                return ResponseEntity.ok(findParkingLotResponseDto);
            }

            throw new InvalidRequestException(
                    "Either (lat,long,radius,vehicleType) OR (city,radius,vehicleType) must be provided."
            );
        } catch (InvalidRequestException ex) {
            log.warn("Invalid parking lot search request: {}", ex.getMessage());
            throw ex;
        } catch (ResourceNotFoundException ex) {
            log.info("No parking lots found for request: {}", ex.getMessage());
            throw ex;
        } catch (ExternalServiceException ex) {
            log.error("External service error while retrieving parking lot data", ex);
            throw ex;
        } catch (Exception ex) {
            log.error("Failed to retrieve parking lot data", ex);
            throw new ExternalServiceException("Unable to fetch parking lot information at this time", ex);
        }
    }


    @GetMapping(value = "/parkingLot/image/{image}", produces = MediaType.IMAGE_JPEG_VALUE)
    public ResponseEntity<byte[]> getImage(@PathVariable("image") String imageName) {
        log.info("Request received to fetch image '{}'", imageName);
        try {
            if (imageName == null || imageName.isBlank()) {
                throw new InvalidRequestException("Image name must be provided");
            }

            byte[] image = objectStorageService.downloadFile(imageName);

            return ResponseEntity.ok(image);
        } catch (InvalidRequestException ex) {
            log.warn("Invalid image download request: {}", ex.getMessage());
            throw ex;
        } catch (ResourceNotFoundException ex) {
            log.info("Requested image '{}' not found", imageName);
            throw ex;
        } catch (StorageException ex) {
            log.error("Storage exception while downloading image '{}'", imageName, ex);
            throw ex;
        } catch (Exception ex) {
            log.error("Failed to download image '{}'", imageName, ex);
            throw new StorageException("Unable to fetch requested image", ex);
        }
    }

}
