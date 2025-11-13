import time
from typing import Callable, Dict, List, Optional, Tuple

import cv2
import numpy as np

from camera_exceptions import ConfigurationError, ImageLoadError, ObjectDetectionError
from logger_config import setup_logger
from objectDetection.camera_recognition_interface import VehicleDetectionInterface

logger = setup_logger("camera_recognition_legacy")


class ObjectRecognition(VehicleDetectionInterface):
    """MobileNetSSD-based vehicle detection implementation."""

    def __init__(
        self,
        *,
        prototxt_path: Optional[str] = None,
        model_path: Optional[str] = None,
        net_loader: Optional[Callable[[str, str], cv2.dnn_Net]] = None,
    ) -> None:
        self.prototxt_path = prototxt_path or "./models/MobileNetSSD_deploy.txt"
        self.model_path = model_path or "./models/MobileNetSSD_deploy.caffemodel"
        self._loader = net_loader or cv2.dnn.readNetFromCaffe

        try:
            self.net = self._loader(self.prototxt_path, self.model_path)
            logger.debug("MobileNet SSD model loaded from %s and %s", self.prototxt_path, self.model_path)
        except Exception as exc:
            logger.exception("Failed to load MobileNet SSD model: %s", exc)
            raise ConfigurationError("Unable to load MobileNet SSD model.") from exc

        self.classes: List[str] = [
            "background",
            "aeroplane",
            "bicycle",
            "bird",
            "boat",
            "bottle",
            "bus",
            "car",
            "cat",
            "chair",
            "cow",
            "diningtable",
            "dog",
            "horse",
            "motorbike",
            "person",
            "pottedplant",
            "sheep",
            "sofa",
            "train",
            "tvmonitor",
        ]

    def predict_cv(self, image: np.ndarray) -> Tuple[List[List[int]], List[str]]:
        (height, width) = image.shape[:2]
        blob = cv2.dnn.blobFromImage(image, 0.007843, (width, height), 127.5)

        try:
            self.net.setInput(blob)
            detections = self.net.forward()
        except Exception as exc:
            logger.exception("MobileNet inference failed: %s", exc)
            raise ObjectDetectionError("MobileNet SSD inference failed.") from exc

        detected_objects: List[str] = []
        bounding_boxes: List[List[int]] = []

        for i in np.arange(0, detections.shape[2]):
            confidence = detections[0, 0, i, 2]
            idx = int(detections[0, 0, i, 1])

            if confidence <= 0.1 or idx >= len(self.classes):
                continue

            label = self.classes[idx]
            if label not in {"car", "bus", "motorbike"}:
                continue

            box = detections[0, 0, i, 3:7] * np.array([width, height, width, height])
            (start_x, start_y, end_x, end_y) = box.astype("int")

            detected_objects.append(f"{label}:{confidence * 100:.2f}%")
            bounding_boxes.append([int(start_x), int(start_y), int(end_x), int(end_y)])

        logger.debug("Detected %d objects", len(detected_objects))
        return bounding_boxes, detected_objects

    @staticmethod
    def generate_vehicle_count(classes: List[str]) -> Dict[str, int]:
        vehicle_count: Dict[str, int] = {}
        for class_name in classes:
            vehicle_type = class_name.split(":", maxsplit=1)[0]
            vehicle_count[vehicle_type] = vehicle_count.get(vehicle_type, 0) + 1
        return vehicle_count

    def capture_and_detect(self, image_path: str, save_image_path: str) -> Tuple[Dict[str, int], List[Dict[str, object]]]:
        image = cv2.imread(image_path)
        if image is None:
            logger.error("Failed to read image %s", image_path)
            raise ImageLoadError(f"Unable to load image: {image_path}")

        resized_image = cv2.resize(image, (900, 900))

        boxes, classes = self.predict_cv(resized_image)
        bounding_boxes: List[Dict[str, object]] = []

        for idx, box in enumerate(boxes):
            vehicle_type = classes[idx].split(":")[0]
            confidence_str = classes[idx].split(":")[1].replace("%", "")
            bounding_boxes.append(
                {
                    "box": box,
                    "class": vehicle_type,
                    "confidence": float(confidence_str),
                }
            )

        processed_image = self.draw_boxes(boxes, classes, resized_image)

        try:
            cv2.imwrite(save_image_path, processed_image)
        except Exception as exc:
            logger.exception("Failed to save processed image %s: %s", save_image_path, exc)
            raise ObjectDetectionError("Unable to save processed image.") from exc

        vehicle_counts = self.generate_vehicle_count(classes)
        logger.info(
            "Detection complete | source=%s | processed=%s | counts=%s",
            image_path,
            save_image_path,
            vehicle_counts,
        )

        time.sleep(1)
        return vehicle_counts, bounding_boxes

    @staticmethod
    def draw_boxes(boxes: List[List[int]], classes: List[str], image: np.ndarray) -> np.ndarray:
        output_image = cv2.cvtColor(np.asarray(image), cv2.COLOR_BGR2RGB)
        for idx, box in enumerate(boxes):
            start_x, start_y, end_x, end_y = box
            cv2.rectangle(output_image, (start_x, start_y), (end_x, end_y), (0, 0, 255), 2)
            cv2.putText(
                output_image,
                classes[idx],
                (start_x, max(start_y - 5, 0)),
                cv2.FONT_HERSHEY_SIMPLEX,
                0.6,
                (0, 255, 0),
                2,
            )
        return output_image
