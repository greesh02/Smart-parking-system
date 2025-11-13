package com.db_writes_service.configs;

import org.apache.kafka.common.TopicPartition;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.core.convert.converter.Converter;

public class TopicPartitionToStringConverter implements Converter<TopicPartition, String> {

    private static final Logger log = LogManager.getLogger(TopicPartitionToStringConverter.class);

    @Override
    public String convert(TopicPartition source) {
        log.debug("Converting TopicPartition({}, {}) to string", source.topic(), source.partition());
        return source.topic() + "-" + source.partition();
    }
}