# Define script and output directories
$scriptDir = Split-Path -Parent $MyInvocation.MyCommand.Definition
$outputDir = "$scriptDir\ConvertSVG"
$inkscape = "inkscape"
$magick = "magick"
$sizes = @(16, 20, 24, 28, 30, 31, 32, 40, 42, 47, 48, 56, 57, 60, 63, 64, 72, 84, 96, 120, 128, 144, 152, 195, 228, 256, 512)

# Create output directory if it doesn't exist
if (-not (Test-Path $outputDir)) {
    New-Item -ItemType Directory -Path $outputDir | Out-Null
}

# Check if Inkscape is available
if (-not (Get-Command $inkscape -ErrorAction SilentlyContinue)) {
    Write-Host "❌ Error: Inkscape not found!" -ForegroundColor Red
    exit 1
}

# Check if ImageMagick (magick) is available
if (-not (Get-Command $magick -ErrorAction SilentlyContinue)) {
    Write-Host "❌ Error: ImageMagick (magick) not found!" -ForegroundColor Red
    exit 1
}

# Find all SVG files
$svgFiles = Get-ChildItem -Path $scriptDir -Filter "*.svg"
if ($svgFiles.Count -eq 0) {
    Write-Host "⚠️ No SVG files found!" -ForegroundColor Yellow
    exit 1
}

# Process each SVG file
foreach ($svg in $svgFiles) {
    $svgPath = $svg.FullName
    $baseName = [System.IO.Path]::GetFileNameWithoutExtension($svgPath)
    Write-Host "🎨 Processing: $baseName.svg" -ForegroundColor Cyan

    # Convert SVG to multiple PNG sizes
    foreach ($size in $sizes) {
        $pngPath = "$outputDir\$baseName-$size.png"
        $relativePngPath = $pngPath.Replace($scriptDir + "\", "")
        
        Write-Host "🔄 Converting to $size x $size... -> $relativePngPath" -ForegroundColor DarkGray
        & $inkscape --export-type=png --export-filename="$pngPath" --export-width=$size --export-height=$size "$svgPath"

        if (Test-Path $pngPath) {
            Write-Host "✅ PNG created: $relativePngPath" -ForegroundColor Green
        }
    }

    # Create ICO file from all generated PNGs
    $icoPath = "$outputDir\$baseName.ico"
    $relativeIcoPath = $icoPath.Replace($scriptDir + "\", "")
    Write-Host "🎨 Creating ICO: $relativeIcoPath" -ForegroundColor Cyan

    # Use ImageMagick to create ICO from all PNGs of the current icon
    $pngFiles = Get-ChildItem -Path $outputDir -Filter "$baseName-*.png"
    if ($pngFiles.Count -gt 0) {
        & $magick $pngFiles.FullName -background none "$icoPath"
    }

    # Check if ICO was created successfully
    if (Test-Path $icoPath) {
        Write-Host "✅ ICO created: $relativeIcoPath" -ForegroundColor Green
    } else {
        Write-Host "❌ Error creating ICO: $relativeIcoPath" -ForegroundColor Red
    }

    Write-Host "----------------------------------------"
}

Write-Host "🎉 All conversions completed! Outputs are in: $outputDir" -ForegroundColor Magenta
