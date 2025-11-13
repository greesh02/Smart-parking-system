import logging
import os
from configparser import ConfigParser, NoOptionError, NoSectionError
from logging.handlers import TimedRotatingFileHandler
from typing import Dict, Optional

from camera_exceptions import ConfigurationError

_CONFIG_CACHE: Optional[ConfigParser] = None
_LOGGING_SECTION = "logging"
_DEFAULT_PATTERN = "%(asctime)s [%(threadName)s] %(levelname)-5s %(name)s - %(message)s"
_DEFAULT_LEVEL = "INFO"
_DEFAULT_DIR = "logs"
_LEVELS: Dict[str, int] = {name.upper(): level for name, level in logging._nameToLevel.items()}


def _load_config() -> ConfigParser:
    global _CONFIG_CACHE

    if _CONFIG_CACHE is not None:
        return _CONFIG_CACHE

    config_path = os.getenv(
        "CAMERA_SERVICE_LOG_CONFIG",
        os.path.join(os.path.dirname(__file__), "logging.conf"),
    )

    parser = ConfigParser(interpolation=None)
    read_files = parser.read(config_path)

    if not read_files:
        parser[_LOGGING_SECTION] = {
            "level": _DEFAULT_LEVEL,
            "pattern": _DEFAULT_PATTERN,
            "directory": _DEFAULT_DIR,
        }

    _CONFIG_CACHE = parser
    return parser


def _resolve_level(level_name: str) -> int:
    if not level_name:
        return logging.INFO

    normalized = level_name.upper()
    if normalized not in _LEVELS:
        raise ConfigurationError(f"Unsupported log level: {level_name}")
    return _LEVELS[normalized]


def setup_logger(name: str) -> logging.Logger:
    config = _load_config()

    try:
        level = _resolve_level(config.get(_LOGGING_SECTION, "level", fallback=_DEFAULT_LEVEL))
        pattern = config.get(_LOGGING_SECTION, "pattern", fallback=_DEFAULT_PATTERN)
        log_dir = config.get(_LOGGING_SECTION, "directory", fallback=_DEFAULT_DIR)
    except (NoSectionError, NoOptionError) as exc:
        raise ConfigurationError(f"Invalid logging configuration: {exc}") from exc

    os.makedirs(log_dir, exist_ok=True)
    log_file = os.path.join(log_dir, f"{name}.log")

    logger = logging.getLogger(name)
    logger.setLevel(level)

    if logger.handlers:
        return logger

    formatter = logging.Formatter(pattern)

    console = logging.StreamHandler()
    console.setFormatter(formatter)
    console.setLevel(level)
    logger.addHandler(console)

    file_handler = TimedRotatingFileHandler(
        log_file, when="midnight", backupCount=10, encoding="utf-8"
    )
    file_handler.setFormatter(formatter)
    file_handler.setLevel(level)
    logger.addHandler(file_handler)

    return logger
