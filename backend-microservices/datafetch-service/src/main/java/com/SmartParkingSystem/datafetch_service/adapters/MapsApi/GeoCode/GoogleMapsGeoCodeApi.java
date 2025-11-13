package com.SmartParkingSystem.datafetch_service.adapters.MapsApi.GeoCode;

import com.SmartParkingSystem.datafetch_service.dtos.GeoCodeRequestDto;
import com.SmartParkingSystem.datafetch_service.dtos.GeoCodeResponseDto;
import com.SmartParkingSystem.datafetch_service.exceptions.ExternalServiceException;
import com.SmartParkingSystem.datafetch_service.exceptions.InvalidRequestException;
import com.SmartParkingSystem.datafetch_service.exceptions.ResourceNotFoundException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.client.RestClientException;
import org.springframework.web.util.UriComponentsBuilder;

@Component
public class GoogleMapsGeoCodeApi implements GeoCodeApiAdapter {

    private static final Logger log = LogManager.getLogger(GoogleMapsGeoCodeApi.class);

    @Value("${geocode.api.key}")
    private String apiKey;

    @Value("${geocode.api.url}")
    private String apiUrl;

    private final RestTemplate restTemplate;

    public GoogleMapsGeoCodeApi(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    @Override
    public GeoCodeResponseDto geocode(GeoCodeRequestDto geoCodeRequestDto) {
        if (geoCodeRequestDto == null || geoCodeRequestDto.address() == null || geoCodeRequestDto.address().isBlank()) {
            throw new InvalidRequestException("Address must be provided for geocoding");
        }

        try {
            String address = geoCodeRequestDto.address();
            log.debug("Requesting geocode for address '{}'", address);

            String url = UriComponentsBuilder
                    .fromHttpUrl(apiUrl)
                    .queryParam("address", address)
                    .queryParam("key", apiKey)
                    .toUriString();

            GoogleMapsGeoCodeResponseDto response =
                    restTemplate.getForObject(url, GoogleMapsGeoCodeResponseDto.class);

            if (response != null && !response.results().isEmpty()) {
                double lat = response.results().get(0).geometry().location().lat();
                double lng = response.results().get(0).geometry().location().lng();

                return new GeoCodeResponseDto(lat, lng);
            }

            throw new ResourceNotFoundException("No coordinates found for address: " + address);
        } catch (InvalidRequestException ex) {
            log.warn("Invalid geocode request: {}", ex.getMessage());
            throw ex;
        } catch (ResourceNotFoundException ex) {
            log.info("Geocode result not found: {}", ex.getMessage());
            throw ex;
        } catch (RestClientException ex) {
            log.error("Error calling Google Geocode API", ex);
            throw new ExternalServiceException("Failed to call Google Geocode API", ex);
        } catch (RuntimeException ex) {
            log.error("Unexpected error while geocoding", ex);
            throw new ExternalServiceException("Failed to process geocode request", ex);
        }
    }
}
