from abc import ABC, abstractmethod
from typing import Dict, List, Tuple

class VehicleDetectionInterface(ABC):
    """Abstract interface for vehicle detection implementations"""
    

    @abstractmethod
    def capture_and_detect(self, image_path: str, save_image_path: str) -> Tuple[Dict, List]:
        """Process image and detect vehicles
        Args:
            image_path: Path to input image
            save_image_path: Path to save processed image
        Returns:
            Tuple of (vehicle_counts, bounding_boxes)
        """
        pass

   