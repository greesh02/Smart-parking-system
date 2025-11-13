package com.db_writes_service.jobs;
import com.db_writes_service.entities.ParkingLot;
import com.db_writes_service.processors.ParkingLotItemProcessor;

import com.mongodb.client.MongoCollection;
import com.mongodb.client.model.*;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.common.TopicPartition;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.bson.Document;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.batch.core.launch.support.TaskExecutorJobLauncher;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.item.ItemWriter;
import org.springframework.batch.item.data.builder.MongoItemWriterBuilder;
import org.springframework.batch.item.kafka.KafkaItemReader;
import org.springframework.batch.item.kafka.builder.KafkaItemReaderBuilder;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.transaction.PlatformTransactionManager;


import java.util.*;

@Configuration
@EnableBatchProcessing
public class ParkingLotJobConfig {

    private static final Logger log = LogManager.getLogger(ParkingLotJobConfig.class);

    @Value("${spring.kafka.bootstrap-servers}")
    private String serverUrl;

    @Value("${kakfa.consumer.topic1}")
    private String topic1;

    @Value("${kafka.consumer.topic1.consumerGroupId1}")
    private String consumerGroupId1Topic1;

    @Value("${kafka.item.reader1}")
    private String itemReaderName1;

    @Value("${mongo.collection1}")
    private String collectionName;

    private final ParkingLotItemProcessor parkingLotItemProcessor;
    private final MongoTemplate mongoTemplate;
    private final PlatformTransactionManager transactionManager;
    private final JobRepository jobRepository;

    public ParkingLotJobConfig(ParkingLotItemProcessor parkingLotItemProcessor, MongoTemplate mongoTemplate, PlatformTransactionManager transactionManager, JobRepository jobRepository) {
        this.parkingLotItemProcessor = parkingLotItemProcessor;
        this.mongoTemplate = mongoTemplate;
        this.transactionManager = transactionManager;
        this.jobRepository = jobRepository;
    }

    @Bean
    public Job importParkingLots(Step fromKafkaToMongoDb) {
        log.info("Creating Job bean 'import-parkingLots'");
        return new JobBuilder("import-parkingLots", jobRepository)
                .incrementer(new RunIdIncrementer())
                .start(fromKafkaToMongoDb)
                .build();
    }

    @Bean
    public Step fromKafkaToMongoDb(ItemWriter<ParkingLot> parkingLotItemWriter,KafkaItemReader<String, String> parkingLotKafkaItemReader) {

        log.info("Creating Step bean 'fromKafkaToMongo'");

        return new StepBuilder("fromKafkaToMongo", jobRepository)
                .<String, ParkingLot>chunk(20, transactionManager)  // chunk size -> if say when job runs and there are 100 messages it splits into 5 chunks and run it
                .reader(parkingLotKafkaItemReader)
                .processor(parkingLotItemProcessor)
                .writer(parkingLotItemWriter)
                .allowStartIfComplete(Boolean.TRUE)
                .build();

    }

    @Bean
    public KafkaItemReader<String, String> parkingLotKafkaItemReader() {
        log.info("Configuring KafkaItemReader for topic {}", topic1);
        var consumerProperties = new Properties();
        consumerProperties.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, serverUrl);
        consumerProperties.put(ConsumerConfig.GROUP_ID_CONFIG, consumerGroupId1Topic1);
        consumerProperties.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
        consumerProperties.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
        consumerProperties.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "latest");
        Map<TopicPartition, Long> offsets = new HashMap<>();
        offsets.put(new TopicPartition(topic1, 0), 0L); // mutable
        return new KafkaItemReaderBuilder<String, String>()
                .name(itemReaderName1)
                .topic(topic1)
                .partitions(List.of(0)) // tells kafka on which partition to read from --> since i created only 1 partition so far this consumer reads only from that (0 based indexing) if say i want this consumer to read from multiple partition --> .partitions(List.of(0, 1, 2))
              .partitionOffsets(
                      offsets // start at end of log
            )
            .consumerProperties(consumerProperties)
            .saveState(true) // store offset after reading
                .build();

    }


//    @Bean
//    public ItemWriter<ParkingLot> parkingLotItemWriter() {
//        var parkingLotItemWriter = new MongoItemWriterBuilder<ParkingLot>()
//                .template(mongoTemplate)
//                .collection(collectionName)
//                .build();
//
//        return new ConditionalBatchWriter(parkingLotItemWriter);
//    }

    /**
     * >>> CHANGE: replaced MongoItemWriter with a custom bulk-upsert ItemWriter that uses
     *             collection.bulkWrite(... UpdateOneModel with $set and upsert:true)
     *
     *  This ensures:
     *   - Only provided (non-null) fields are $set (partial update)
     *   - Upsert true will create document if missing
     *   - Uses bulkWrite for performance
     */
    @Bean
    public ItemWriter<ParkingLot> parkingLotItemWriter() {

        ItemWriter<ParkingLot> bulkUpsertWriter = items -> {
            if (items == null || items.isEmpty()) return;

            log.debug("Preparing bulk upsert for {} items into collection '{}'", items.size(), collectionName);
            MongoCollection<Document> collection = mongoTemplate.getCollection(collectionName);
            List<WriteModel<Document>> ops = new ArrayList<>();

            for (ParkingLot lot : items) {

                Document updateDoc = new Document();


                if (lot.getPosition() != null) {
                    // Mongo stores GeoJsonPoint as {"type":"Point","coordinates":[x,y]}
                    Document pos = new Document();
                    pos.append("type", lot.getPosition().getType());
                    pos.append("coordinates", lot.getPosition().getCoordinates());
                    updateDoc.append("position", pos);
                }

                if (lot.getStreetAddress() != null)
                    updateDoc.append("streetAddress", lot.getStreetAddress());

                if (lot.getLandmark() != null)
                    updateDoc.append("landmark", lot.getLandmark());

                if (lot.getCity() != null)
                    updateDoc.append("city", lot.getCity());

                if (lot.getState() != null)
                    updateDoc.append("state", lot.getState());

                if (lot.getPostalCode() != null)
                    updateDoc.append("postalCode", lot.getPostalCode());

                if (lot.getCountry() != null)
                    updateDoc.append("country", lot.getCountry());

                // --- Images ---
                if (lot.getImageUrlOriginal() != null)
                    updateDoc.append("imageUrlOriginal", lot.getImageUrlOriginal());

                if (lot.getImageUrlProcessed() != null)
                    updateDoc.append("imageUrlProcessed", lot.getImageUrlProcessed());

                // --- Nested objects ---
                if (lot.getSlotInfo() != null)
                    updateDoc.append("slotInfo", mongoTemplate.getConverter().convertToMongoType(lot.getSlotInfo()));

                if (lot.getLastUpdated() != null)
                    updateDoc.append("lastUpdated", mongoTemplate.getConverter().convertToMongoType(lot.getLastUpdated()));

                if (lot.getAiDescription() != null)
                    updateDoc.append("aiDescription", lot.getAiDescription());

                // ========= UPSERT OPERATION =========
                if (!updateDoc.isEmpty()) {
                    ops.add(new UpdateOneModel<>(
                            Filters.eq("_id", lot.getLotId()),
                            new Document("$set", updateDoc),
                            new UpdateOptions().upsert(true)
                    ));
                }
            }

            if (!ops.isEmpty()) {
                log.info("Executing bulkWrite with {} operations into collection '{}'", ops.size(), collectionName);
                collection.bulkWrite(ops, new BulkWriteOptions().ordered(false));
            } else {
                log.debug("No operations to execute for this batch");
            }
        };

        return new ConditionalBatchWriter(bulkUpsertWriter);
    }


    @Bean
    public JobLauncher jobLauncher(JobRepository jobRepository) throws Exception {
        log.info("Creating TaskExecutorJobLauncher");
        TaskExecutorJobLauncher launcher = new TaskExecutorJobLauncher();
        launcher.setJobRepository(jobRepository);
        launcher.afterPropertiesSet();
        return launcher;
    }
}