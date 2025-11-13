import json
import os
from typing import Any, Dict

import numpy as np
from kafka import KafkaProducer

from camera_exceptions import ConfigurationError, KafkaPublishError
from logger_config import setup_logger

logger = setup_logger("camera_service_kafka_producer")


class KafkaPublisher:
    """Kafka producer wrapper for publishing camera service events."""

    def __init__(self) -> None:
        bootstrap = os.getenv("KAFKA_BROKER", "localhost:9092")
        topic = os.getenv("KAFKA_TOPIC", "messageUpload")

        if "KAFKA_BROKER" not in os.environ:
            logger.warning("KAFKA_BROKER not set; defaulting to %s", bootstrap)
        if "KAFKA_TOPIC" not in os.environ:
            logger.warning("KAFKA_TOPIC not set; defaulting to %s", topic)

        if not bootstrap:
            raise ConfigurationError("Kafka bootstrap server configuration is empty.")
        if not topic:
            raise ConfigurationError("Kafka topic configuration is empty.")

        self.topic = topic

        try:
            self.producer = KafkaProducer(
                bootstrap_servers=bootstrap,
                value_serializer=lambda v: json.dumps(v, default=self.numpy_encoder).encode("utf-8"),
            )
            logger.debug("Kafka producer initialized for topic %s at %s", self.topic, bootstrap)
        except Exception as exc:  # pragma: no cover - kafka library specific
            logger.exception("Failed to initialize Kafka producer: %s", exc)
            raise ConfigurationError("Unable to initialize Kafka producer.") from exc

    @staticmethod
    def numpy_encoder(obj: Any) -> Any:
        if isinstance(obj, np.integer):
            return int(obj)
        if isinstance(obj, np.floating):
            return float(obj)
        if isinstance(obj, np.ndarray):
            return obj.tolist()
        raise TypeError(f"Object of type {type(obj)} is not JSON serializable")

    def publish(self, message: Dict[str, Any]) -> None:
        try:
            self.producer.send(self.topic, message)
            self.producer.flush()
            logger.info("Message published to %s", self.topic)
        except Exception as exc:
            logger.exception("Kafka publish failed: %s", exc)
            raise KafkaPublishError("Failed to publish message to Kafka.") from exc
