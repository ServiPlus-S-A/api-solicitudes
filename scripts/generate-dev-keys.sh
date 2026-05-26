#!/usr/bin/env sh
set -eu
KEY_DIR="$(cd "$(dirname "$0")/.." && pwd)/docker/keys"
mkdir -p "$KEY_DIR"
openssl genrsa -out "$KEY_DIR/private.pem" 2048
openssl rsa -in "$KEY_DIR/private.pem" -pubout -out "$KEY_DIR/public.pem"
chmod 600 "$KEY_DIR/private.pem"
echo "Claves generadas en $KEY_DIR (no commitear private.pem)"
