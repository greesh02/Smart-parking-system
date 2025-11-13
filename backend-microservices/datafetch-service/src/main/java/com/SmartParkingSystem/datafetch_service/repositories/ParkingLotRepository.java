package com.SmartParkingSystem.datafetch_service.repositories;

import com.SmartParkingSystem.datafetch_service.entities.ParkingLot;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
@Repository
public interface ParkingLotRepository extends MongoRepository<ParkingLot, String> {

    @Query("""
    {
        position: {
            $geoWithin: {
                $centerSphere: [
                    [?0, ?1],
                    ?2
                ]
            }
        },
        ?#{'slotInfo.availableSlotsCount.' + #vehicleType}: { $gt: 0 }
    }
""")
    List<ParkingLot> findAvailableSlotsWithinRadius(
            double longitude,
            double latitude,
            double radiusInRadians,
            String vehicleType,
            Pageable pageable
    );
}
