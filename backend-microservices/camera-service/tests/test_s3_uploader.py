import os

import pytest

from camera_exceptions import ConfigurationError, S3UploadError
from s3_uploader import S3Uploader


@pytest.fixture()
def aws_env(monkeypatch, tmp_path):
    monkeypatch.setenv("AWS_ACCESS_KEY_ID", "test-access")
    monkeypatch.setenv("AWS_SECRET_ACCESS_KEY", "test-secret")
    monkeypatch.setenv("AWS_REGION", "us-east-1")
    monkeypatch.setenv("S3_BUCKET_NAME", "test-bucket")
    temp_file = tmp_path / "image.jpg"
    temp_file.write_bytes(b"fake-image-content")
    return temp_file


@pytest.mark.parametrize("case", [pytest.param("upload-succeeds", id="upload-succeeds")])
def test_upload_image_success(monkeypatch, mocker, aws_env, case):
    mock_client = mocker.Mock()
    monkeypatch.setattr("s3_uploader.boto3.client", lambda *args, **kwargs: mock_client)

    uploader = S3Uploader()
    result = uploader.upload_image(str(aws_env), "lot-42")

    assert mock_client.upload_file.called
    assert result.endswith("lot-42.jpg")
    assert case == "upload-succeeds"


@pytest.mark.parametrize("missing_env", [pytest.param("S3_BUCKET_NAME", id="missing-bucket-config")])
def test_missing_configuration_raises_error(monkeypatch, missing_env):
    for key in ("AWS_ACCESS_KEY_ID", "AWS_SECRET_ACCESS_KEY", "S3_BUCKET_NAME"):
        monkeypatch.delenv(key, raising=False)

    if missing_env != "S3_BUCKET_NAME":
        monkeypatch.setenv("S3_BUCKET_NAME", "test-bucket")
    if missing_env != "AWS_ACCESS_KEY_ID":
        monkeypatch.setenv("AWS_ACCESS_KEY_ID", "key")
    if missing_env != "AWS_SECRET_ACCESS_KEY":
        monkeypatch.setenv("AWS_SECRET_ACCESS_KEY", "secret")

    with pytest.raises(ConfigurationError):
        S3Uploader()


@pytest.mark.parametrize("error", [pytest.param(RuntimeError("network unreachable"), id="upload-failure-raises-custom-error")])
def test_upload_failure_raises_custom_error(monkeypatch, mocker, aws_env, error):
    mock_client = mocker.Mock()
    mock_client.upload_file.side_effect = error
    monkeypatch.setattr("s3_uploader.boto3.client", lambda *args, **kwargs: mock_client)

    uploader = S3Uploader()

    with pytest.raises(S3UploadError):
        uploader.upload_image(str(aws_env), "lot-42")

