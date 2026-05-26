#!/bin/sh
set -eu

ISSUER="${JWT_KONG_ISSUER:-traceability-dev}"
PUB_FILE="/keys/public.pem"
OUT_FILE="/kong/kong.yml"

if [ ! -f "$PUB_FILE" ]; then
  echo "ERROR: No se encontró $PUB_FILE. Ejecuta: ./scripts/generate-dev-keys.sh" >&2
  exit 1
fi

{
  printf '_format_version: "3.0"\n\n'
  printf 'consumers:\n'
  printf '  - username: traceability\n'
  printf '    jwt_secrets:\n'
  printf '      - key: %s\n' "$ISSUER"
  printf '        algorithm: RS256\n'
  printf '        rsa_public_key: |\n'
  sed 's/^/          /' "$PUB_FILE"
  printf '\n'
  cat /kong/kong.services.yml
} > "$OUT_FILE"

echo "Kong declarative config generada en $OUT_FILE (issuer=$ISSUER)"
exec /docker-entrypoint.sh kong docker-start
