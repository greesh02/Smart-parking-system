from datetime import datetime
from pathlib import Path

import pytest

from camera_exceptions import ImageDirectoryError
from main import IMAGE_EXTENSIONS, _discover_images, process_image


@pytest.mark.parametrize(
    "files",
    [
        pytest.param(["a.jpg", "b.png"], id="discover-images-with-supported-extensions"),
    ],
)
def test_discover_images_success(tmp_path, files):
    for filename in files:
        (tmp_path / filename).write_bytes(b"data")

    result = _discover_images(tmp_path, IMAGE_EXTENSIONS)

    assert len(result) == len(files)
    assert all(isinstance(item, Path) for item in result)


@pytest.mark.parametrize(
    "scenario",
    [
        pytest.param("missing-directory", id="discover-images-missing-directory"),
        pytest.param("no-images", id="discover-images-no-images"),
    ],
)
def test_discover_images_errors(tmp_path, scenario):
    if scenario == "missing-directory":
        with pytest.raises(ImageDirectoryError):
            _discover_images(tmp_path / "missing", IMAGE_EXTENSIONS)
    else:
        with pytest.raises(ImageDirectoryError):
            _discover_images(tmp_path, IMAGE_EXTENSIONS)


@pytest.mark.parametrize(
    "image_name",
    [
        pytest.param("lot-001.jpg", id="process-image-success"),
    ],
)
def test_process_image_pipeline(monkeypatch, mocker, tmp_path, image_name):
    image_path = tmp_path / image_name
    image_path.write_bytes(b"fake-image")
    processed_dir = tmp_path / "processed"

    recognizer = mocker.Mock()
    recognizer.capture_and_detect.return_value = (
        {"car": 1},
        [{"class": "car", "box": [0, 0, 10, 10], "confidence": 0.99}],
    )

    uploader = mocker.Mock()
    uploader.upload_image.side_effect = ["original-url", "processed-url"]

    publisher = mocker.Mock()

    fixed_time = datetime(2025, 1, 1, 12, 0, 0)

    class FixedDatetime:
        @staticmethod
        def utcnow():
            return fixed_time

    monkeypatch.setattr("main.datetime", FixedDatetime)

    process_image(image_path, processed_dir, recognizer, uploader, publisher)

    recognizer.capture_and_detect.assert_called_once_with(str(image_path), mocker.ANY)
    assert uploader.upload_image.call_count == 2
    publisher.publish.assert_called_once()
    payload = publisher.publish.call_args.args[0]
    assert payload["lastUpdated"]["cameraService"] == "2025-01-01 12:00:00"
    assert processed_dir.exists()

