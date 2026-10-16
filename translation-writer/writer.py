"""
Translation Writer Service
===========================
Consumes  : dms.documents.translated
Writes to : PostgreSQL (translations table)

This is a separate microservice — NOT the translator.
Separation of concerns: translator only translates, writer only persists.
"""

import os
import json
import time
import logging
import psycopg2
from kafka import KafkaConsumer

logging.basicConfig(
    level=logging.INFO,
    format="%(asctime)s [%(levelname)s] %(message)s"
)
log = logging.getLogger(__name__)

KAFKA_BOOTSTRAP = os.environ["KAFKA_BOOTSTRAP_SERVERS"]
SOURCE_TOPIC    = "dms.documents.translated"
CONSUMER_GROUP  = "translation-writer-service"

DB_URL = os.environ["DB_URL"]
# DB_URL format: postgresql://user:password@host:5432/dbname


def get_connection():
    """Create PostgreSQL connection with retry."""
    for attempt in range(10):
        try:
            conn = psycopg2.connect(DB_URL)
            log.info("Connected to PostgreSQL")
            return conn
        except Exception as e:
            log.warning("DB connection attempt %d failed: %s", attempt + 1, e)
            time.sleep(5)
    raise RuntimeError("Cannot connect to PostgreSQL after 10 attempts")


def ensure_table(conn):
    """Create translations table if it does not exist."""
    with conn.cursor() as cur:
        cur.execute("""
            CREATE TABLE IF NOT EXISTS translations (
                id               SERIAL PRIMARY KEY,
                document_uid     TEXT NOT NULL,
                source_document_id BIGINT,
                original_title   TEXT,
                translated_title TEXT,
                target_language  TEXT,
                model            TEXT,
                created_at       TIMESTAMP DEFAULT NOW()
            )
        """)
        conn.commit()
    log.info("translations table ready")


def save_translation(conn, event):
    """Insert translation record into PostgreSQL."""
    with conn.cursor() as cur:
        cur.execute("""
            INSERT INTO translations
                (document_uid, source_document_id, original_title,
                 translated_title, target_language, model)
            VALUES (%s, %s, %s, %s, %s, %s)
        """, (
            event.get("uid"),
            event.get("sourceDocumentId"),
            event.get("originalTitle"),
            event.get("translatedTitle"),
            event.get("targetLanguage"),
            event.get("model"),
        ))
        conn.commit()


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
    log.info("Translation Writer Service starting")
    log.info("Consuming from: %s", SOURCE_TOPIC)

    conn = get_connection()
    ensure_table(conn)
    consumer = make_consumer()

    log.info("Waiting for translated messages...")

    for message in consumer:
        event = message.value
        uid = event.get("uid", "unknown")
        log.info("Received translation for uid=%s: '%s' -> '%s'",
                 uid, event.get("originalTitle"), event.get("translatedTitle"))
        try:
            save_translation(conn, event)
            consumer.commit()
            log.info("Saved translation for uid=%s to PostgreSQL", uid)
        except Exception as e:
            log.error("Failed to save translation for uid=%s: %s", uid, e)
            # Reconnect on DB failure
            try:
                conn = get_connection()
                ensure_table(conn)
            except Exception as re:
                log.error("Reconnect failed: %s", re)


if __name__ == "__main__":
    main()
