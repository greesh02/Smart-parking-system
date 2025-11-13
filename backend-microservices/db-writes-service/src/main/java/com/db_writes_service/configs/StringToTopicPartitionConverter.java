package com.db_writes_service.configs;


import org.apache.kafka.common.TopicPartition;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.core.convert.converter.Converter;

public class StringToTopicPartitionConverter implements Converter<String, TopicPartition> {

    private static final Logger log = LogManager.getLogger(StringToTopicPartitionConverter.class);

    @Override
    public TopicPartition convert(String source) {
        log.debug("Converting string '{}' to TopicPartition", source);
        String[] parts = source.split("-");
        return new TopicPartition(parts[0], Integer.parseInt(parts[1]));
    }
}