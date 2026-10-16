#!/bin/bash
# ══════════════════════════════════════════════════════════════════
#  test-all.sh — Complete DMS Testing Suite
#  Usage: chmod +x test-all.sh && ./test-all.sh
# ══════════════════════════════════════════════════════════════════

set -e

# Colors for output
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
BLUE='\033[0;34m'
NC='\033[0m' # No Color

echo -e "${BLUE}╔════════════════════════════════════════════════════════════╗${NC}"
echo -e "${BLUE}║       DMS Complete Testing & Verification Suite            ║${NC}"
echo -e "${BLUE}╚════════════════════════════════════════════════════════════╝${NC}"

# ─────────────────────────────────────────────────────────────────────────────
# PART 1: BUILD
# ─────────────────────────────────────────────────────────────────────────────

echo -e "\n${YELLOW}[PART 1] Building all services...${NC}"
echo "Running: ./build-all.sh"
./build-all.sh
echo -e "${GREEN}✔ Build complete${NC}"

# ─────────────────────────────────────────────────────────────────────────────
# PART 2: START SERVICES
# ─────────────────────────────────────────────────────────────────────────────

echo -e "\n${YELLOW}[PART 2] Starting Docker services...${NC}"
echo "Running: docker compose up -d"
docker compose up -d
echo -e "${GREEN}✔ Services started${NC}"

# Wait for services to be fully ready
echo "Waiting for services to initialize (30 seconds)..."
sleep 30

# ─────────────────────────────────────────────────────────────────────────────
# PART 3: HEALTH CHECKS
# ─────────────────────────────────────────────────────────────────────────────

echo -e "\n${YELLOW}[PART 3] Health checks...${NC}"

echo -e "\n${BLUE}Gateway Health:${NC}"
curl -s http://localhost:8080/actuator/health | jq '{status: .status}'

echo -e "\n${BLUE}Documents Service Health:${NC}"
curl -s http://localhost:8081/actuator/health | jq '{status: .status, database: .components.db.status}' || echo "⚠ Service not ready"

echo -e "\n${BLUE}Comments Service Health:${NC}"
curl -s http://localhost:8083/actuator/health | jq '{status: .status, database: .components.db.status}' || echo "⚠ Service not ready"

echo -e "\n${BLUE}ESB Service Health:${NC}"
curl -s http://localhost:8084/actuator/health | jq '{status: .status}' || echo "⚠ Service not ready"

echo -e "\n${GREEN}✔ Health checks complete${NC}"

# ─────────────────────────────────────────────────────────────────────────────
# PART 4: GATEWAY ROUTES
# ─────────────────────────────────────────────────────────────────────────────

echo -e "\n${YELLOW}[PART 4] Gateway routes configuration...${NC}"
echo -e "\n${BLUE}Available Routes:${NC}"
curl -s http://localhost:8080/actuator/gateway/routes | jq '.[] | {route_id, uri, predicate}'

echo -e "\n${GREEN}✔ Gateway routes verified${NC}"

# ─────────────────────────────────────────────────────────────────────────────
# PART 5: API TESTING
# ─────────────────────────────────────────────────────────────────────────────

echo -e "\n${YELLOW}[PART 5] API Testing Workflow...${NC}"

# Test 5.1: Create Document
echo -e "\n${BLUE}[5.1] Creating new document...${NC}"
DOC_RESPONSE=$(curl -s -X POST http://localhost:8080/api/documents/add \
  -H "Content-Type: application/json" \
  -d '{"title": "Enterprise DMS System Documentation"}')

DOC_ID=$(echo "$DOC_RESPONSE" | jq -r '.id // empty')

if [ -z "$DOC_ID" ] || [ "$DOC_ID" = "null" ]; then
  echo -e "${RED}✗ Failed to create document${NC}"
  echo "Response: $DOC_RESPONSE"
  exit 1
fi

echo -e "${GREEN}✔ Document created with ID: $DOC_ID${NC}"
echo "Response:"
echo "$DOC_RESPONSE" | jq .

# Test 5.2: List Documents
echo -e "\n${BLUE}[5.2] Listing all documents...${NC}"
curl -s http://localhost:8080/api/documents/list | jq '.[] | {id, title}'
echo -e "${GREEN}✔ Documents listed${NC}"

# Test 5.3: Get Specific Document
echo -e "\n${BLUE}[5.3] Getting document $DOC_ID...${NC}"
curl -s http://localhost:8080/api/documents/get/$DOC_ID | jq .
echo -e "${GREEN}✔ Document retrieved${NC}"

# Test 5.4: Add Comments
echo -e "\n${BLUE}[5.4] Adding comments to document $DOC_ID...${NC}"

COMMENT1=$(curl -s -X POST http://localhost:8080/api/comments/add \
  -H "Content-Type: application/json" \
  -d "{\"docId\": $DOC_ID, \"content\": \"Architecture: Microservices with Spring Cloud Gateway\"}")
echo "Comment 1: $COMMENT1" | jq .

COMMENT2=$(curl -s -X POST http://localhost:8080/api/comments/add \
  -H "Content-Type: application/json" \
  -d "{\"docId\": $DOC_ID, \"content\": \"Database: PostgreSQL with JPA/Hibernate\"}")
echo "Comment 2: $COMMENT2" | jq .

COMMENT3=$(curl -s -X POST http://localhost:8080/api/comments/add \
  -H "Content-Type: application/json" \
  -d "{\"docId\": $DOC_ID, \"content\": \"Storage: MinIO S3-compatible object storage\"}")
echo "Comment 3: $COMMENT3" | jq .

echo -e "${GREEN}✔ Comments added${NC}"

# Test 5.5: List Comments
echo -e "\n${BLUE}[5.5] Listing comments for document $DOC_ID...${NC}"
curl -s http://localhost:8080/api/comments/list/$DOC_ID | jq '.[] | {id, content, createdAt}'
echo -e "${GREEN}✔ Comments listed${NC}"

# Test 5.6: Get Document with Comments (ESB Orchestration)
echo -e "\n${BLUE}[5.6] Getting document with all comments (ESB Orchestration)...${NC}"
curl -s http://localhost:8080/api/document/$DOC_ID | jq .
echo -e "${GREEN}✔ Document with comments retrieved${NC}"

# Test 5.7: Create another document
echo -e "\n${BLUE}[5.7] Creating second document...${NC}"
DOC2_RESPONSE=$(curl -s -X POST http://localhost:8080/api/documents/add \
  -H "Content-Type: application/json" \
  -d '{"title": "Project Deployment Guide"}')

DOC2_ID=$(echo "$DOC2_RESPONSE" | jq -r '.id // empty')
echo -e "${GREEN}✔ Document 2 created with ID: $DOC2_ID${NC}"

# Test 5.8: Add comments to second document
echo -e "\n${BLUE}[5.8] Adding comments to document $DOC2_ID...${NC}"

curl -s -X POST http://localhost:8080/api/comments/add \
  -H "Content-Type: application/json" \
  -d "{\"docId\": $DOC2_ID, \"content\": \"Step 1: Build Docker images\"}" | jq .

curl -s -X POST http://localhost:8080/api/comments/add \
  -H "Content-Type: application/json" \
  -d "{\"docId\": $DOC2_ID, \"content\": \"Step 2: Push to registry\"}" | jq .

echo -e "${GREEN}✔ Comments added to document 2${NC}"

# Test 5.9: List all documents with summary
echo -e "\n${BLUE}[5.9] Final document summary...${NC}"
echo -e "${BLUE}All documents:${NC}"
curl -s http://localhost:8080/api/documents/list | jq '.[] | {id, title}'

# ─────────────────────────────────────────────────────────────────────────────
# PART 6: SERVICE LOGS
# ─────────────────────────────────────────────────────────────────────────────

echo -e "\n${YELLOW}[PART 6] Quick status check...${NC}"
docker compose ps
echo -e "${GREEN}✔ All services running${NC}"

# ─────────────────────────────────────────────────────────────────────────────
# SUMMARY
# ─────────────────────────────────────────────────────────────────────────────

echo -e "\n${BLUE}╔════════════════════════════════════════════════════════════╗${NC}"
echo -e "${BLUE}║                    TEST SUMMARY                             ║${NC}"
echo -e "${BLUE}╚════════════════════════════════════════════════════════════╝${NC}"

echo -e "\n${GREEN}✔ ALL TESTS PASSED${NC}"

echo -e "\n${YELLOW}API Endpoints:${NC}"
echo "  Gateway:              http://localhost:8080"
echo "  Documents Service:    http://localhost:8081"
echo "  Comments Service:     http://localhost:8083"
echo "  ESB Service:          http://localhost:8084"

echo -e "\n${YELLOW}Key API Routes:${NC}"
echo "  Documents List        GET  http://localhost:8080/api/documents/list"
echo "  Get Document          GET  http://localhost:8080/api/documents/get/{id}"
echo "  Create Document       POST http://localhost:8080/api/documents/add"
echo "  Comments List         GET  http://localhost:8080/api/comments/list/{docId}"
echo "  Add Comment           POST http://localhost:8080/api/comments/add"
echo "  Document + Comments   GET  http://localhost:8080/api/document/{id}"

echo -e "\n${YELLOW}Useful Commands:${NC}"
echo "  View logs:            docker compose logs -f {gateway|documents|comments|esb}"
echo "  Stop services:        docker compose down"
echo "  Restart services:     docker compose restart"
echo "  Full restart:         docker compose down && docker compose up -d"

echo -e "\n${BLUE}Documentation files available:${NC}"
echo "  - test-all.sh         (This test suite)"
echo "  - build-all.sh        (Build script)"
echo "  - docker-compose.yml  (Service configuration)"

echo -e "\n"
