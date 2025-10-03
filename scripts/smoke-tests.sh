\
#!/usr/bin/env bash
# Smoke tests for Chat App stack (Frontend, Backend, Keycloak)
# Usage:
#   ./scripts/smoke-tests.sh                 # uses $TOKEN if exported
#   TOKEN=eyJ... ./scripts/smoke-tests.sh    # supply token inline
#   ./scripts/smoke-tests.sh --token eyJ...  # supply token as arg
#   ./scripts/smoke-tests.sh --host localhost --api 9080 --front 8888 --kc 8081

set -u

HOST="localhost"
API_PORT="9080"
FRONT_PORT="8888"
KC_PORT="8081"
TOKEN="${TOKEN:-}"
CURL_OPTS=(-s -S -i)

echo TOKEN=$TOKEN

while [[ $# -gt 0 ]]; do
  case "$1" in
    --host) HOST="$2"; shift 2;;
    --api) API_PORT="$2"; shift 2;;
    --front) FRONT_PORT="$2"; shift 2;;
    --kc) KC_PORT="$2"; shift 2;;
    --token) TOKEN="$2"; shift 2;;
    *) echo "Unknown arg: $1"; exit 2;;
  esac
done

FRONT_URL="http://${HOST}:${FRONT_PORT}"
API_URL="http://${HOST}:${API_PORT}"
KC_URL="http://${HOST}:${KC_PORT}"

GREEN='\033[0;32m'; YELLOW='\033[1;33m'; RED='\033[0;31m'; NC='\033[0m'

hr() { printf '%*s\n' 80 | tr ' ' '-'; }

status_line() {
  local label="$1"; local code="$2"
  if [[ "$code" =~ ^2[0-9][0-9]$ ]]; then
    echo -e "✅ ${GREEN}${label}${NC} → ${code}"
  elif [[ "$code" =~ ^3[0-9][0-9]$ ]]; then
    echo -e "ℹ️  ${YELLOW}${label}${NC} → ${code}"
  else
    echo -e "❌ ${RED}${label}${NC} → ${code}"
  fi
}

http_code() {
  local url="$1"; shift
  # Return only HTTP status code (last response if redirects)
  curl -s -o /dev/null -w "%{http_code}" "$url" "$@"
}

echo "Chat App — Smoke tests"
echo "Front: $FRONT_URL  •  API: $API_URL  •  Keycloak: $KC_URL"
hr

# 0) Reachability checks
c_front=$(http_code "$FRONT_URL/")
status_line "Frontend reachable" "$c_front"

c_api_health=$(http_code "$API_URL/actuator/health")
status_line "Backend health" "$c_api_health"

c_kc=$(http_code "$KC_URL/realms/demo/.well-known/openid-configuration")
status_line "Keycloak discovery" "$c_kc"

hr

# 1) CORS preflight
echo "CORS preflight OPTIONS /api/messages (Origin: $FRONT_URL)"
pre=$(curl -s -o /dev/null -w "%{http_code}" -X OPTIONS "$API_URL/api/messages" \
  -H "Origin: $FRONT_URL" \
  -H "Access-Control-Request-Method: GET")
status_line "CORS preflight" "$pre"

# 2) Unauth GET (should be 401)
unauth=$(http_code "$API_URL/api/messages")
status_line "GET /api/messages without token" "$unauth"

# 3) Auth GET with token (if provided)
if [[ -n "$TOKEN" ]]; then
  auth=$(curl -s -o /dev/null -w "%{http_code}" "$API_URL/api/messages" -H "Authorization: Bearer $TOKEN")
  status_line "GET /api/messages with token" "$auth"
else
  echo -e "ℹ️  ${YELLOW}No TOKEN provided${NC} — skip authenticated request."
  echo "    Export a token: TOKEN=eyJ... ./scripts/smoke-tests.sh"
fi

# 4) POST a message (if token provided)
if [[ -n "$TOKEN" ]]; then
  post=$(curl -s -o /dev/null -w "%{http_code}" -X POST "$API_URL/api/messages" \
    -H "Authorization: Bearer $TOKEN" -H "Content-Type: application/json" \
    -d '{"text":"hello from smoke test"}')
  status_line "POST /api/messages with token" "$post"
fi

hr
echo "Tips:"
echo "- If CORS preflight fails → check CORS_ALLOWED_ORIGINS and OPTIONS/** permitAll"
echo "- If 401 with token → ensure issuer matches OIDC_ISSUER_URI and JWK URL is reachable from backend"
echo "- Use docs/troubleshooting.md for a full decision tree"
