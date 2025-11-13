package com.db_writes_service.processors.mappers;

import com.db_writes_service.dtos.MessageProcessedEventDto;
import com.db_writes_service.entities.ParkingLot;
//import org.mapstruct.Mapper;
//import org.mapstruct.factory.Mappers;

public interface ParkingLotMapper {
//    ParkingLotMapper INSTANCE = Mappers.getMapper(ParkingLotMapper.class);

    ParkingLot mapToEventToEntity(MessageProcessedEventDto messageProcessedEventDto);
}
