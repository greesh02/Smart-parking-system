package com.db_writes_service.jobs;

import com.db_writes_service.entities.ParkingLot;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.batch.item.Chunk;
import org.springframework.batch.item.ItemWriter;

// only reason for this piece is to avoid repeated single writes
public class ConditionalBatchWriter implements ItemWriter<ParkingLot> {

    private static final Logger log = LogManager.getLogger(ConditionalBatchWriter.class);

    private static final int MINIMUM_BATCH_SIZE = 1;

    // The actual Mongo writer you want to call
    private final ItemWriter<ParkingLot> delegateWriter;

    public ConditionalBatchWriter(ItemWriter<ParkingLot> delegateWriter) {
        this.delegateWriter = delegateWriter;
    }


    @Override
    public void write(Chunk<? extends ParkingLot> items) throws Exception {
        if (items.size() > MINIMUM_BATCH_SIZE) {
            log.info("Batch size is {}. Writing to MongoDB.", items.size());
            delegateWriter.write(items);
        } else {
            log.debug("Batch size is {} (<= {}). Skipping write operation.", items.size(), MINIMUM_BATCH_SIZE);
        }
    }
}