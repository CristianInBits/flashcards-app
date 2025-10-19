Param(
  [Parameter(Mandatory=$true)][string]$BackupPath,
  [string]$ContainerName = "flashcards_db"
)

if (-not (Test-Path $BackupPath)) {
  Write-Error "No existe el archivo: $BackupPath"
  exit 1
}

$RootDir = (Resolve-Path "$PSScriptRoot\..").Path
$EnvFile = Join-Path $RootDir ".env"

if (Test-Path $EnvFile) {
  Get-Content $EnvFile | Where-Object { $_ -match '^[A-Za-z_][A-Za-z0-9_]*=.*$' } | ForEach-Object {
    $kv = $_.Split('=',2)
    $name = $kv[0]
    $value = $kv[1]
    $value = $value.Trim('"').Trim("'")
    $env:$name = $value
  }
}

$POSTGRES_DB = $env:POSTGRES_DB
if (-not $POSTGRES_DB) { $POSTGRES_DB = "flashcards" }
$POSTGRES_USER = $env:POSTGRES_USER
if (-not $POSTGRES_USER) { $POSTGRES_USER = "flash" }

# Verificar contenedor corriendo
$running = docker ps --format '{{.Names}}' | Select-String -SimpleMatch $ContainerName
if (-not $running) {
  Write-Error "El contenedor '$ContainerName' no está en ejecución."
  exit 1
}

$ext = [System.IO.Path]::GetExtension($BackupPath).ToLowerInvariant()

Write-Host "⚠️  Esto SOBREESCRIBIRÁ objetos en la BD '$POSTGRES_DB' del contenedor '$ContainerName'."
$confirm = Read-Host "¿Continuar? (yes/no)"
if ($confirm -ne "yes") {
  Write-Host "Cancelado."
  exit 0
}

if ($ext -eq ".dump" -or $ext -eq ".backup") {
  Write-Host "🔁 Restaurando con pg_restore (formato personalizado)..."
  Get-Content -Encoding Byte $BackupPath | docker exec -i $ContainerName pg_restore -U $POSTGRES_USER -d $POSTGRES_DB --clean --if-exists
} elseif ($ext -eq ".sql") {
  Write-Host "🔁 Restaurando SQL plano con psql..."
  Get-Content $BackupPath | docker exec -i $ContainerName psql -U $POSTGRES_USER -d $POSTGRES_DB
} else {
  Write-Error "Extensión no reconocida: $ext (usa .dump/.backup para pg_restore o .sql para psql)"
  exit 1
}

Write-Host "✅ Restore completado."
