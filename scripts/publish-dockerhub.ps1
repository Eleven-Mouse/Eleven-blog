param(
    [string]$Namespace = "kunxingll",
    [string]$Tag = "latest"
)

$ErrorActionPreference = "Stop"

$root = Split-Path -Parent $PSScriptRoot
$backendImage = "$Namespace/blog-backend:$Tag"
$frontendImage = "$Namespace/blog-view:$Tag"

Write-Host "[docker] building backend image: $backendImage"
docker build -t $backendImage -f "$root\blog-backend\Dockerfile" "$root\blog-backend"

Write-Host "[docker] building frontend image: $frontendImage"
docker build -t $frontendImage -f "$root\blog-view\Dockerfile" "$root\blog-view"

Write-Host "[docker] pushing backend image: $backendImage"
docker push $backendImage

Write-Host "[docker] pushing frontend image: $frontendImage"
docker push $frontendImage

Write-Host "[docker] done"
