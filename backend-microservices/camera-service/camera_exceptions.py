class CameraServiceError(Exception):
    """Base exception for camera service errors."""


class ConfigurationError(CameraServiceError):
    """Raised when configuration is invalid or missing."""


class ImageLoadError(CameraServiceError):
    """Raised when an image cannot be loaded from disk."""


class ObjectDetectionError(CameraServiceError):
    """Raised when object detection fails."""


class S3UploadError(CameraServiceError):
    """Raised when uploading to S3 fails."""


class KafkaPublishError(CameraServiceError):
    """Raised when publishing to Kafka fails."""


class ImageDirectoryError(CameraServiceError):
    """Raised when image directory processing fails."""

