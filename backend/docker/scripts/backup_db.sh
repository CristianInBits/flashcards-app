#!/usr/bin/env bash
set -euo pipefail

# Config
ROOT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")/.." && pwd)"
ENV_FILE="$ROOT_DIR/.env"
BACKUP_DIR="$ROOT_DIR/backups"
CONTAINER_NAME="${1:-flashcards_db}"   # Permite pasar otro nombre: ./backup_db.sh my_db_container

# Carga variables desde .env si existe
if [[ -f "$ENV_FILE" ]]; then
  # exporta solo líneas tipo KEY=VALUE (sin espacios)
  export $(grep -E '^[A-Za-z_][A-Za-z0-9_]*=' "$ENV_FILE" | xargs) || true
fi

POSTGRES_DB="${POSTGRES_DB:-flashcards}"
POSTGRES_USER="${POSTGRES_USER:-flash}"

mkdir -p "$BACKUP_DIR"
STAMP="$(date +%Y%m%d-%H%M%S)"
OUTFILE="$BACKUP_DIR/${POSTGRES_DB}-${STAMP}.dump"

echo "📦 Haciendo backup de '$POSTGRES_DB' desde contenedor '$CONTAINER_NAME' a:"
echo "   $OUTFILE"

# Verifica que el contenedor existe/está corriendo
if ! docker ps --format '{{.Names}}' | grep -q "^${CONTAINER_NAME}$"; then
  echo "❌ El contenedor '${CONTAINER_NAME}' no está en ejecución. Arráncalo primero."
  exit 1
fi

# Dump en formato personalizado (-F c) para usar pg_restore
docker exec -t "$CONTAINER_NAME" pg_dump -U "$POSTGRES_USER" -d "$POSTGRES_DB" -F c > "$OUTFILE"

echo "✅ Backup completado: $OUTFILE"
echo "ℹ️  Para restaurar: ./restore_db.sh \"$OUTFILE\""
