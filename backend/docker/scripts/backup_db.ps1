Param(
  [string]$ContainerName = "flashcards_db"
)

$RootDir = (Resolve-Path "$PSScriptRoot\..").Path
$EnvFile = Join-Path $RootDir ".env"
$BackupDir = Join-Path $RootDir "backups"

# Cargar .env si existe (solo líneas KEY=VALUE sin espacios)
if (Test-Path $EnvFile) {
  Get-Content $EnvFile | Where-Object { $_ -match '^[A-Za-z_][A-Za-z0-9_]*=.*$' } | ForEach-Object {
    $kv = $_.Split('=',2)
    $name = $kv[0]
    $value = $kv[1]
    # Evita comillas alrededor
    $value = $value.Trim('"').Trim("'")
    $env:$name = $value
  }
}

$POSTGRES_DB = $env:POSTGRES_DB
if (-not $POSTGRES_DB) { $POSTGRES_DB = "flashcards" }
$POSTGRES_USER = $env:POSTGRES_USER
if (-not $POSTGRES_USER) { $POSTGRES_USER = "flash" }

if (-not (Test-Path $BackupDir)) { New-Item -ItemType Directory -Path $BackupDir | Out-Null }

$stamp = Get-Date -Format "yyyyMMdd-HHmmss"
$outfile = Join-Path $BackupDir "$POSTGRES_DB-$stamp.dump"

Write-Host "📦 Haciendo backup de '$POSTGRES_DB' desde contenedor '$ContainerName' a:"
Write-Host "   $outfile"

# Verificar contenedor corriendo
$running = docker ps --format '{{.Names}}' | Select-String -SimpleMatch $ContainerName
if (-not $running) {
  Write-Error "El contenedor '$ContainerName' no está en ejecución."
  exit 1
}

# Dump en formato personalizado
docker exec -t $ContainerName pg_dump -U $POSTGRES_USER -d $POSTGRES_DB -F c | Set-Content -Encoding Byte $outfile

Write-Host "✅ Backup completado: $outfile"
Write-Host "ℹ️  Para restaurar: .\restore_db.ps1 `"$outfile`""
