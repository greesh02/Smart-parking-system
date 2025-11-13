package com.db_writes_service.processors;

import com.db_writes_service.dtos.MessageProcessedEventDto;
import com.db_writes_service.entities.ParkingLot;
import com.db_writes_service.processors.mappers.ParkingLotMapper;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.stereotype.Component;

@Component
public class ParkingLotItemProcessor implements ItemProcessor<String, ParkingLot> {

    private static final Logger log = LogManager.getLogger(ParkingLotItemProcessor.class);

    private final ObjectMapper objectMapper;
    private final ParkingLotMapper parkingLotMapper;


    public ParkingLotItemProcessor(ObjectMapper objectMapper, ParkingLotMapper parkingLotMapper) {
        this.objectMapper = objectMapper;
        this.parkingLotMapper = parkingLotMapper;
    }


    @Override
    public ParkingLot process(String data) throws Exception {

        log.debug("Received raw message payload, size={} bytes", data != null ? data.length() : 0);
        MessageProcessedEventDto messageProcessedEventDto = objectMapper.readValue(data, MessageProcessedEventDto.class);
        log.info("Processing lotId={}, imgOriginal={}", messageProcessedEventDto.lotId(), messageProcessedEventDto.imageUrlOriginal());
        return parkingLotMapper.mapToEventToEntity(messageProcessedEventDto);
//        return null;
    }
}