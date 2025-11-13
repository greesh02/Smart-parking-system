package com.db_writes_service.jobs;

import com.db_writes_service.entities.ParkingLot;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.batch.item.Chunk;
import org.springframework.batch.item.ItemWriter;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

import static org.assertj.core.api.Assertions.assertThat;

class ConditionalBatchWriterTest {

    @Test
     @DisplayName("ConditionalBatchWriter skips delegate when batch size <= minimum")
    void skips_whenBatchSizeIsLessOrEqualMinimum() throws Exception {
        AtomicInteger writeCount = new AtomicInteger(0);
        ItemWriter<ParkingLot> delegate = items -> writeCount.incrementAndGet();
        ConditionalBatchWriter writer = new ConditionalBatchWriter(delegate);

        List<ParkingLot> items = new ArrayList<>();
        items.add(new ParkingLot());
        Chunk<ParkingLot> ch = new Chunk<>(items);
        writer.write(ch);

        assertThat(writeCount.get()).isEqualTo(0);
    }

    @Test
     @DisplayName("ConditionalBatchWriter delegates when batch size > minimum")
    void delegates_whenBatchSizeGreaterThanMinimum() throws Exception {
        AtomicInteger writeCount = new AtomicInteger(0);
        ItemWriter<ParkingLot> delegate = items -> writeCount.incrementAndGet();
        ConditionalBatchWriter writer = new ConditionalBatchWriter(delegate);

        List<ParkingLot> items = new ArrayList<>();
        items.add(new ParkingLot());
        items.add(new ParkingLot());
        Chunk<ParkingLot> ch = new Chunk<>(items);
        writer.write(ch);

        assertThat(writeCount.get()).isEqualTo(1);
    }
}


