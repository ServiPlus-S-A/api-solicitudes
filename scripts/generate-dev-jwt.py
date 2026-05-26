#!/usr/bin/env python3
"""Genera un JWT RS256 de desarrollo compatible con Kong y Spring Resource Server."""
import argparse
import datetime
import sys
from pathlib import Path

try:
    import jwt
except ImportError:
    print("Instala PyJWT: pip install PyJWT", file=sys.stderr)
    sys.exit(1)


def main() -> None:
    parser = argparse.ArgumentParser(description="Genera JWT de desarrollo TraceAbility")
    parser.add_argument("--private-key", default="docker/keys/private.pem")
    parser.add_argument("--issuer", default="traceability-dev")
    parser.add_argument("--sub", default="dev-user")
    parser.add_argument("--roles", default="COORDINADOR,GERENTE")
    parser.add_argument("--hours", type=int, default=8)
    args = parser.parse_args()

    key_path = Path(args.private_key)
    if not key_path.exists():
        print(f"No existe {key_path}. Ejecuta ./scripts/generate-dev-keys.sh", file=sys.stderr)
        sys.exit(1)

    private_key = key_path.read_text()
    roles = [r.strip() for r in args.roles.split(",") if r.strip()]
    now = datetime.datetime.now(datetime.UTC)
    payload = {
        "iss": args.issuer,
        "sub": args.sub,
        "roles": roles,
        "iat": now,
        "exp": now + datetime.timedelta(hours=args.hours),
    }
    token = jwt.encode(payload, private_key, algorithm="RS256")
    print(token)


if __name__ == "__main__":
    main()
