#!/bin/bash
DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
export PORT="${PORT:-8080}"
echo "Starting Zephyr Temporary Backend on port $PORT..."
exec "$DIR/venv/bin/python" "$DIR/server.py"
