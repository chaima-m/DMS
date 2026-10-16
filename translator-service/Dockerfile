FROM python:3.11-slim

WORKDIR /app

COPY requirements.txt .
RUN pip install --no-cache-dir -r requirements.txt

COPY translator.py .

# GEMINI_API_KEY is NEVER baked into the image.
# It is injected at runtime via environment variable.
CMD ["python", "-u", "translator.py"]
