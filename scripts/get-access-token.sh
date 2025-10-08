#!/usr/bin/env bash
# get-access-token.sh — récupère un access_token Keycloak (flux password) -- DEV UNIQUEMENT
# Usage:
#   ./scripts/get-access-token.sh                          # user=user pass=user client=chat-app realm=demo kc=http://localhost:8081
#   ./scripts/get-access-token.sh --user alice --pass s3cr3t
#   ./scripts/get-access-token.sh --client chat-app --realm demo --kc http://localhost:8081
#
# Sortie: imprime UNIQUEMENT l'access_token sur stdout (idéal pour `TOKEN=$(...)`)

set -euo pipefail

USER="user"
PASS="user"
CLIENT_ID="chat-app"
REALM="demo"
KC_URL="http://localhost:8081"

while [[ $# -gt 0 ]]; do
  case "$1" in
    --user) USER="$2"; shift 2;;
    --pass) PASS="$2"; shift 2;;
    --client|--client-id) CLIENT_ID="$2"; shift 2;;
    --realm) REALM="$2"; shift 2;;
    --kc|--kc-url) KC_URL="$2"; shift 2;;
    -h|--help)
      echo "Usage: $0 [--user u] [--pass p] [--client chat-app] [--realm demo] [--kc http://localhost:8081]"
      exit 0;;
    *) echo "Unknown arg: $1" >&2; exit 2;;
  esac
done

TOKEN_ENDPOINT="${KC_URL}/realms/${REALM}/protocol/openid-connect/token"

RESP=$(curl -sS -X POST "$TOKEN_ENDPOINT" \
  -H "Content-Type: application/x-www-form-urlencoded" \
  -d "grant_type=password" \
  -d "client_id=${CLIENT_ID}" \
  -d "username=${USER}" \
  -d "password=${PASS}")

# Extraire access_token (jq si dispo, sinon fallback awk)
if command -v jq >/dev/null 2>&1; then
  echo "$RESP" | jq -r '.access_token'
else
  echo "$RESP" | awk -F'"' '/access_token/ {print $4; exit}'
fi
