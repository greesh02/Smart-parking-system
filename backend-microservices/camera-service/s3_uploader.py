import os
from datetime import datetime
from typing import Optional

import boto3
from botocore.exceptions import BotoCoreError, ClientError

from camera_exceptions import ConfigurationError, S3UploadError
from logger_config import setup_logger

logger = setup_logger("s3_uploader")


class S3Uploader:
    """AWS S3 uploader helper for camera service."""

    def __init__(self) -> None:
        self.bucket = os.getenv("S3_BUCKET_NAME")
        if not self.bucket:
            raise ConfigurationError("S3_BUCKET_NAME environment variable is required.")

        region = os.getenv("AWS_REGION", "ap-south-1")
        access_key = os.getenv("AWS_ACCESS_KEY_ID")
        secret_key = os.getenv("AWS_SECRET_ACCESS_KEY")

        if not access_key or not secret_key:
            raise ConfigurationError("AWS credentials are not fully configured.")

        try:
            self.s3 = boto3.client(
                "s3",
                aws_access_key_id=access_key,
                aws_secret_access_key=secret_key,
                region_name=region,
            )
            logger.debug("S3 client initialized for bucket %s in region %s", self.bucket, region)
        except Exception as exc:  # pragma: no cover - boto3 internal
            logger.exception("Failed to initialize S3 client: %s", exc)
            raise ConfigurationError("Unable to initialize S3 client.") from exc

    def upload_image(self, image_path: str, image_key: str, *, content_type: Optional[str] = "image/jpeg") -> str:
        try:
            file_key = f"{datetime.utcnow().strftime('%Y%m%d_%H%M%S')}_{image_key}.jpg"
            logger.debug("Uploading %s to bucket %s as %s", image_path, self.bucket, file_key)
            extra_args = {"ContentType": content_type} if content_type else None
            if extra_args:
                self.s3.upload_file(image_path, self.bucket, file_key, ExtraArgs=extra_args)
            else:
                self.s3.upload_file(image_path, self.bucket, file_key)
            s3_url = file_key
            logger.info("S3 upload successful | local=%s | key=%s", image_path, file_key)
            return s3_url
        except (BotoCoreError, ClientError, FileNotFoundError, Exception) as exc:
            logger.exception("Failed to upload image %s: %s", image_path, exc)
            raise S3UploadError(f"Failed to upload {image_path} to S3.") from exc
