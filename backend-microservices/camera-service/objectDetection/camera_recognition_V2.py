import os
from typing import Callable, Dict, List, Optional, Tuple

import cv2

from camera_exceptions import ConfigurationError, ImageLoadError, ObjectDetectionError
from logger_config import setup_logger
from objectDetection.camera_recognition_interface import VehicleDetectionInterface
from ultralytics import YOLO

logger = setup_logger("camera_recognition")


class ObjectRecognition(VehicleDetectionInterface):
    """YOLO-based vehicle detection implementation."""

    def __init__(
        self,
        *,
        model_path: Optional[str] = None,
        model_loader: Optional[Callable[[str], object]] = None,
    ) -> None:
        self.model_path = model_path or os.getenv("YOLO_MODEL_PATH", "./models/yolo11n.pt")
        self._loader = model_loader or self._default_loader

        try:
            self.net = self._loader(self.model_path)
            logger.debug("YOLO model loaded from %s", self.model_path)
        except Exception as exc:
            logger.exception("Failed to load YOLO model: %s", exc)
            raise ConfigurationError(f"Unable to load YOLO model from {self.model_path}") from exc

        self.classes: Dict[int, str] = {
            2: "car",
            3: "motorbike",
            5: "bus",
        }

    @staticmethod
    def _default_loader(path: str) -> object:
        # if not os.path.exists(path):
        #     raise FileNotFoundError(path)
        return YOLO(path)

    def capture_and_detect(self, image_path: str, save_image_path: str) -> Tuple[Dict[str, int], List[Dict[str, object]]]:
        img = cv2.imread(image_path)
        if img is None:
            logger.error("Failed to load image from path %s", image_path)
            raise ImageLoadError(f"Unable to load image: {image_path}")

        vehicle_counts = {"car": 0, "motorbike": 0, "bus": 0}
        vehicle_boxes: List[Dict[str, object]] = []

        try:
            results = self.net(img)[0]
        except Exception as exc:
            logger.exception("YOLO inference failed for %s: %s", image_path, exc)
            raise ObjectDetectionError("YOLO inference failed.") from exc

        for box in getattr(results, "boxes", []):
            cls_id = int(box.cls[0])
            confidence = float(box.conf[0])
            x1, y1, x2, y2 = (float(coord) for coord in box.xyxy[0])

            if cls_id not in self.classes:
                continue

            name = self.classes[cls_id]
            vehicle_counts[name] += 1
            vehicle_boxes.append(
                {
                    "class": name,
                    "box": [int(x1), int(y1), int(x2), int(y2)],
                    "confidence": confidence,
                }
            )

            cv2.rectangle(img, (int(x1), int(y1)), (int(x2), int(y2)), (0, 0, 255), 2)
            cv2.putText(
                img,
                f"{name} {confidence:.2f}",
                (int(x1), int(y1) - 10),
                cv2.FONT_HERSHEY_SIMPLEX,
                0.6,
                (0, 255, 0),
                2,
            )

        try:
            cv2.imwrite(save_image_path, img)
        except Exception as exc:
            logger.exception("Failed to write processed image %s: %s", save_image_path, exc)
            raise ObjectDetectionError(f"Unable to save processed image: {save_image_path}") from exc

        logger.info(
            "Detection complete | source=%s | processed=%s | counts=%s",
            image_path,
            save_image_path,
            vehicle_counts,
        )

        return vehicle_counts, vehicle_boxes
