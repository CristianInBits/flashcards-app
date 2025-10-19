#!/usr/bin/env bash
set -euo pipefail

if [[ $# -lt 1 ]]; then
  echo "Uso: $0 <ruta-al-backup> [nombre_contenedor]"
  echo "Ej:  $0 ../backups/flashcards-20251018-230000.dump  flashcards_db"
  exit 1
fi

BACKUP_PATH="$1"
CONTAINER_NAME="${2:-flashcards_db}"

ROOT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")/.." && pwd)"
ENV_FILE="$ROOT_DIR/.env"

if [[ ! -f "$BACKUP_PATH" ]]; then
  echo "❌ No existe el archivo: $BACKUP_PATH"
  exit 1
fi

# Carga .env si existe
if [[ -f "$ENV_FILE" ]]; then
  export $(grep -E '^[A-Za-z_][A-Za-z0-9_]*=' "$ENV_FILE" | xargs) || true
fi

POSTGRES_DB="${POSTGRES_DB:-flashcards}"
POSTGRES_USER="${POSTGRES_USER:-flash}"

# Verifica contenedor
if ! docker ps --format '{{.Names}}' | grep -q "^${CONTAINER_NAME}$"; then
  echo "❌ El contenedor '${CONTAINER_NAME}' no está en ejecución."
  exit 1
fi

EXT="${BACKUP_PATH##*.}"

echo "⚠️  Esto SOBREESCRIBIRÁ objetos en la BD '$POSTGRES_DB' del contenedor '$CONTAINER_NAME'."
read -p "¿Continuar? (yes/no): " yn
if [[ "$yn" != "yes" ]]; then
  echo "Cancelado."
  exit 0
fi

if [[ "$EXT" == "dump" || "$EXT" == "backup" ]]; then
  # Formato personalizado de pg_dump -> pg_restore
  echo "🔁 Restaurando con pg_restore (formato personalizado)..."
  cat "$BACKUP_PATH" | docker exec -i "$CONTAINER_NAME" pg_restore -U "$POSTGRES_USER" -d "$POSTGRES_DB" --clean --if-exists
elif [[ "$EXT" == "sql" ]]; then
  # SQL plano
  echo "🔁 Restaurando SQL plano con psql..."
  cat "$BACKUP_PATH" | docker exec -i "$CONTAINER_NAME" psql -U "$POSTGRES_USER" -d "$POSTGRES_DB"
else
  echo "❌ Extensión no reconocida: .$EXT (usa .dump/.backup para pg_restore o .sql para psql)"
  exit 1
fi

echo "✅ Restore completado."
