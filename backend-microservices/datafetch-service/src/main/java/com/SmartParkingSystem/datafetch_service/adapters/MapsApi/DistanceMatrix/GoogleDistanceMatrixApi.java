package com.SmartParkingSystem.datafetch_service.adapters.MapsApi.DistanceMatrix;

import com.SmartParkingSystem.datafetch_service.dtos.*;
import com.SmartParkingSystem.datafetch_service.exceptions.ExternalServiceException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.client.RestClientException;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class GoogleDistanceMatrixApi implements DistanceMatrixApi {

    private static final Logger log = LogManager.getLogger(GoogleDistanceMatrixApi.class);

    @Value("${distance.api.key}")
    private String apiKey;

    @Value("${distance.api.url}")
    private String apiUrl;

    private final RestTemplate restTemplate;

    public GoogleDistanceMatrixApi(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    public DistanceMatrixResponseDto getDistanceMatrix(DistanceMatrixRequestDto distanceMatrixRequestDto) {
        LatLng source = distanceMatrixRequestDto.source();
        List<LatLng> destinations = distanceMatrixRequestDto.destinations();
        String travelMode = distanceMatrixRequestDto.travelMode();
        log.debug("Calling Google Distance Matrix API for {} destinations", destinations.size());

        try {
            List<OriginOrDestination> origins = List.of(
                    new OriginOrDestination(
                            new WayPoint(
                                    new Location(source)
                            )
                    )
            );

            List<OriginOrDestination> destList = destinations.stream()
                    .map(d -> new OriginOrDestination(
                            new WayPoint(
                                    new Location(d)
                            )
                    ))
                    .toList();

            GoogleDistanceMatrixRequestDto requestBody = new GoogleDistanceMatrixRequestDto(
                    origins,
                    destList,
                    travelMode,
                    "TRAFFIC_AWARE"
            );

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            headers.set("X-Goog-Api-Key", apiKey);
            headers.set("X-Goog-FieldMask",
                    "originIndex,destinationIndex,distanceMeters,duration,status,condition");

            HttpEntity<GoogleDistanceMatrixRequestDto> entity = new HttpEntity<>(requestBody, headers);

            ResponseEntity<GoogleDistanceMatrixResponseDto[]> response =
                    restTemplate.exchange(
                            apiUrl,
                            HttpMethod.POST,
                            entity,
                            GoogleDistanceMatrixResponseDto[].class
                    );

            GoogleDistanceMatrixResponseDto[] requestDtos = response.getBody();
            if (requestDtos == null) {
                throw new ExternalServiceException("Distance matrix API returned an empty response");
            }
            return fromGoogleResponses(requestDtos);
        } catch (RestClientException ex) {
            log.error("Error calling Google Distance Matrix API", ex);
            throw new ExternalServiceException("Failed to call Google Distance Matrix API", ex);
        }
    }

    private DistanceMatrixResponseDto fromGoogleResponses(GoogleDistanceMatrixResponseDto[] googleResponses) {

        List<DistanceMatrixResponseItem> items =
                Arrays.stream(googleResponses)
                        .map(r -> new DistanceMatrixResponseItem(
                                r.originIndex(),
                                r.destinationIndex(),
                                r.distanceMeters(),
                                r.duration(),
                                r.condition()
                        ))
                        .collect(Collectors.toList());

        return new DistanceMatrixResponseDto(items);
    }
}
