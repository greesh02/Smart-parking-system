package com.db_writes_service.processors.mappers;

import com.db_writes_service.dtos.MessageProcessedEventDto;
import com.db_writes_service.dtos.SlotCountInfo;
import com.db_writes_service.entities.Count;
import com.db_writes_service.entities.LastUpdated;
import com.db_writes_service.entities.ParkingLot;
import com.db_writes_service.entities.SlotInfo;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Component;

@Component
public class ParkingLotMapperImpl implements ParkingLotMapper {

    private static final Logger log = LogManager.getLogger(ParkingLotMapperImpl.class);

    @Override
    public ParkingLot mapToEventToEntity(MessageProcessedEventDto messageProcessedEventDto) {
        if ( messageProcessedEventDto == null ) {
            log.warn("Received null MessageProcessedEventDto in mapper");
            return null;
        }

        log.debug("Mapping MessageProcessedEventDto to ParkingLot entity for lotId={}", messageProcessedEventDto.lotId());
        ParkingLot parkingLot = new ParkingLot();
        parkingLot.setImageUrlOriginal(messageProcessedEventDto.imageUrlOriginal());
        parkingLot.setImageUrlProcessed(messageProcessedEventDto.imageUrlProcessed());

        parkingLot.setLotId( messageProcessedEventDto.lotId() );
        parkingLot.setSlotInfo( slotInfoToSlotInfo( messageProcessedEventDto.slotInfo() ) );
        parkingLot.setLastUpdated( lastUpdatedToLastUpdated( messageProcessedEventDto.lastUpdated() ) );
        parkingLot.setAiDescription( messageProcessedEventDto.aiDescription() );


        return parkingLot;
    }

    protected Count slotCountInfoToCount(SlotCountInfo slotCountInfo) {
        if ( slotCountInfo == null ) {
            return null;
        }

        Count count = new Count();
        count.setMotorbike(slotCountInfo.motorBike());
        count.setCar( slotCountInfo.car() );
        count.setBus( slotCountInfo.bus() );

        return count;
    }

    protected SlotInfo slotInfoToSlotInfo(MessageProcessedEventDto.SlotInfo slotInfo) {
        if ( slotInfo == null ) {
            return null;
        }

        SlotInfo slotInfo1 = new SlotInfo();

        slotInfo1.setAvailableSlotsCount( slotCountInfoToCount( slotInfo.availableSlotsCount() ) );
        slotInfo1.setOccupiedSlotsCount( slotCountInfoToCount( slotInfo.occupiedSlotsCount() ) );

        return slotInfo1;
    }

    protected LastUpdated lastUpdatedToLastUpdated(MessageProcessedEventDto.LastUpdated lastUpdated) {
        if ( lastUpdated == null ) {
            return null;
        }

        LastUpdated lastUpdated1 = new LastUpdated();

        lastUpdated1.setCameraService( lastUpdated.cameraService() );
        lastUpdated1.setAiService( lastUpdated.aiService() );

        return lastUpdated1;
    }
}
