import os
from datetime import datetime
from pathlib import Path
from typing import Iterable, List

from dotenv import load_dotenv

from camera_exceptions import CameraServiceError, ImageDirectoryError
from kafka_producer import KafkaPublisher
from logger_config import setup_logger
from objectDetection.camera_recognition_V2 import ObjectRecognition
from s3_uploader import S3Uploader

logger = setup_logger("main")
load_dotenv(".env")

IMAGE_EXTENSIONS = {".jpg", ".jpeg", ".png", ".bmp", ".gif", ".webp"}


def _discover_images(directory: Path, extensions: Iterable[str]) -> List[Path]:
    if not directory.exists() or not directory.is_dir():
        raise ImageDirectoryError(f"Image directory does not exist: {directory}")

    images = sorted(path for path in directory.iterdir() if path.suffix.lower() in extensions)
    if not images:
        raise ImageDirectoryError(f"No images found in directory: {directory}")
    return images


def _build_processed_path(processed_dir: Path, original_name: str) -> Path:
    processed_dir.mkdir(parents=True, exist_ok=True)
    utc_time = datetime.utcnow().strftime("%Y-%m-%d_%H.%M.%S")
    return processed_dir / f"{utc_time}_{original_name}"


def process_image(image_path: Path, processed_dir: Path, recognizer: ObjectRecognition, uploader: S3Uploader, publisher: KafkaPublisher) -> None:
    lot_id = image_path.stem
    processed_path = _build_processed_path(processed_dir, image_path.name)

    vehicle_counts, bounding_boxes = recognizer.capture_and_detect(str(image_path), str(processed_path))

    original_url = uploader.upload_image(str(image_path), lot_id)
    processed_url = uploader.upload_image(str(processed_path), f"processed_{lot_id}")

    payload = {
        "event": "messageUpload",
        "lotId": lot_id,
        "imageUrlOriginal": original_url,
        "imageUrlProcessed": processed_url,
        "slotInfo": {"occupiedSlotsCount": vehicle_counts},
        "boundingBoxes": {"occupiedSlots": bounding_boxes},
        "lastUpdated": {"cameraService": datetime.utcnow().strftime("%Y-%m-%d %H:%M:%S")},
    }

    publisher.publish(payload)
    logger.info("Processed and published data for lot %s", lot_id)


def main() -> None:
    images_dir = Path(os.getenv("IMAGES_DIR", "images/images"))
    processed_dir = Path(os.getenv("PROCESSED_IMAGES_DIR", "images/processed-images"))

    try:
        images = _discover_images(images_dir, IMAGE_EXTENSIONS)
        recognizer = ObjectRecognition()
        uploader = S3Uploader()
        publisher = KafkaPublisher()

        for image_path in images:
            try:
                process_image(image_path, processed_dir, recognizer, uploader, publisher)
            except CameraServiceError as exc:
                logger.error("Failed to process image %s: %s", image_path, exc)
            except Exception as exc:  # pragma: no cover - unexpected errors
                logger.exception("Unexpected error processing image %s: %s", image_path, exc)
                raise
    except CameraServiceError as exc:
        logger.error("Camera service failed during initialization: %s", exc)
        raise


if __name__ == "__main__":
    main()
