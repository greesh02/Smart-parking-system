import os

import pytest

from camera_exceptions import KafkaPublishError
from kafka_producer import KafkaPublisher


@pytest.fixture(autouse=True)
def kafka_env(monkeypatch):
    monkeypatch.setenv("KAFKA_BROKER", "localhost:9092")
    monkeypatch.setenv("KAFKA_TOPIC", "camera-topic")


@pytest.mark.parametrize("message", [pytest.param({"hello": "world"}, id="publish-successfully")])
def test_publish_success(monkeypatch, mocker, message):
    mock_producer = mocker.Mock()
    monkeypatch.setattr("kafka_producer.KafkaProducer", lambda **kwargs: mock_producer)

    publisher = KafkaPublisher()
    publisher.publish(message)

    mock_producer.send.assert_called_once_with("camera-topic", message)
    mock_producer.flush.assert_called_once()


@pytest.mark.parametrize("exception", [pytest.param(RuntimeError("fail"), id="publish-failure-raises-custom-error")])
def test_publish_failure(monkeypatch, mocker, exception):
    mock_producer = mocker.Mock()
    mock_producer.send.side_effect = exception
    monkeypatch.setattr("kafka_producer.KafkaProducer", lambda **kwargs: mock_producer)

    publisher = KafkaPublisher()

    with pytest.raises(KafkaPublishError):
        publisher.publish({"foo": "bar"})

