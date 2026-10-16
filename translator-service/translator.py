import os
import json
import time
import logging
import requests
from kafka import KafkaConsumer, KafkaProducer

logging.basicConfig(level=logging.INFO, format="%(asctime)s [%(levelname)s] %(message)s")
log = logging.getLogger(__name__)

KAFKA_BOOTSTRAP = os.environ["KAFKA_BOOTSTRAP_SERVERS"]
GEMINI_API_KEY  = os.environ["GEMINI_API_KEY"]
SOURCE_TOPIC    = "dms.documents.uploaded"
TARGET_TOPIC    = "dms.documents.translated"
CONSUMER_GROUP  = "translator-service"
TARGET_LANGUAGE = os.environ.get("TARGET_LANGUAGE", "French")
GEMINI_MODEL = "gemini-2.5-flash"
MAX_RETRIES     = 5

GEMINI_URL = (
    f"https://generativelanguage.googleapis.com/v1beta/models/"
    f"{GEMINI_MODEL}:generateContent?key={GEMINI_API_KEY}"
)


def call_gemini(title: str) -> str:
    prompt = (
        f"Translate the following document title to {TARGET_LANGUAGE}. "
        f"Return ONLY the translated title, no explanation, no quotes.\n\n"
        f"Title: {title}"
    )
    payload = {"contents": [{"parts": [{"text": prompt}]}]}

    for attempt in range(1, MAX_RETRIES + 1):
        try:
            resp = requests.post(
                GEMINI_URL,
                json=payload,
                headers={"Content-Type": "application/json"},
                timeout=15
            )

            # Handle 429 rate limit — wait longer
            if resp.status_code == 429:
                wait = 30
                log.warning("Rate limited (429). Waiting %ds before retry %d/%d...",
                            wait, attempt, MAX_RETRIES)
                time.sleep(wait)
                continue

            resp.raise_for_status()
            data = resp.json()
            return data["candidates"][0]["content"]["parts"][0]["text"].strip()

        except requests.exceptions.HTTPError as e:
            log.warning("HTTP error attempt %d/%d: %s", attempt, MAX_RETRIES, e)
            if attempt < MAX_RETRIES:
                time.sleep(5 * attempt)
        except Exception as e:
            log.warning("Gemini attempt %d/%d failed: %s", attempt, MAX_RETRIES, e)
            if attempt < MAX_RETRIES:
                time.sleep(5 * attempt)

    raise RuntimeError(f"Gemini unreachable after {MAX_RETRIES} attempts")


def make_producer():
    return KafkaProducer(
        bootstrap_servers=KAFKA_BOOTSTRAP,
        value_serializer=lambda v: json.dumps(v).encode("utf-8"),
        key_serializer=lambda k: k.encode("utf-8") if k else None,
        acks="all",
        retries=5,
    )


def make_consumer():
    return KafkaConsumer(
        SOURCE_TOPIC,
        bootstrap_servers=KAFKA_BOOTSTRAP,
        group_id=CONSUMER_GROUP,
        auto_offset_reset="earliest",
        enable_auto_commit=False,
        value_deserializer=lambda v: json.loads(v.decode("utf-8")),
        key_deserializer=lambda k: k.decode("utf-8") if k else None,
        session_timeout_ms=60000,
        heartbeat_interval_ms=10000,
    )


def main():
    log.info("Translator service starting")
    log.info("Model: %s | Language: %s", GEMINI_MODEL, TARGET_LANGUAGE)
    log.info("Source: %s -> Target: %s", SOURCE_TOPIC, TARGET_TOPIC)

    producer = make_producer()
    consumer = make_consumer()

    log.info("Connected to Kafka. Waiting for messages...")

    for message in consumer:
        event = message.value
        uid   = message.key or event.get("uid", "unknown")
        title = event.get("title", "")

        log.info("Received document uid=%s title='%s'", uid, title)

        try:
            translated = call_gemini(title)
            log.info("Translated: '%s' -> '%s'", title, translated)

            result_event = {
                "uid":              uid,
                "originalTitle":    title,
                "translatedTitle":  translated,
                "targetLanguage":   TARGET_LANGUAGE,
                "sourceDocumentId": event.get("id"),
                "fileKey":          event.get("fileKey"),
                "model":            GEMINI_MODEL,
            }

            producer.send(TARGET_TOPIC, key=uid, value=result_event)
            producer.flush()
            log.info("Published translation for uid=%s", uid)

            consumer.commit()
            log.info("Offset committed for uid=%s", uid)

        except RuntimeError as e:
            log.error("FATAL: %s -- offset NOT committed, will retry.", e)
        except Exception as e:
            log.error("Unexpected error for uid=%s: %s", uid, e)


if __name__ == "__main__":
    main()
