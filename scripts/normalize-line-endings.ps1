param(
    [string]$RepoRoot = (Resolve-Path (Join-Path $PSScriptRoot '..')).Path,
    [switch]$WhatIf
)

$ErrorActionPreference = 'Stop'

function Get-GitFiles {
    param([string]$Root)

    $output = & git -C $Root ls-files --cached --others --exclude-standard -z
    if ($LASTEXITCODE -ne 0) {
        throw 'Nepodarilo se nacist seznam souboru z git ls-files.'
    }

    return ($output -split "`0" | Where-Object { $_ })
}

function Get-TargetLineEnding {
    param(
        [string]$Root,
        [string]$RelativePath
    )

    $attrOutput = & git -C $Root check-attr eol -- $RelativePath
    if ($LASTEXITCODE -ne 0) {
        throw "Nepodarilo se zjistit git atribut eol pro '$RelativePath'."
    }

    if ($attrOutput -match ': eol: (.+)$') {
        $value = $Matches[1].Trim()
        if ($value -eq 'lf' -or $value -eq 'crlf') {
            return $value
        }
    }

    switch ([IO.Path]::GetExtension($RelativePath).ToLowerInvariant()) {
        '.bat' { return 'crlf' }
        '.cmd' { return 'crlf' }
        '.ps1' { return 'crlf' }
        default { return 'lf' }
    }
}

function Test-IsBinary {
    param([byte[]]$Bytes)

    foreach ($byte in $Bytes) {
        if ($byte -eq 0) {
            return $true
        }
    }

    return $false
}

function Get-FileEncoding {
    param([byte[]]$Bytes)

    if ($Bytes.Length -ge 3 -and $Bytes[0] -eq 0xEF -and $Bytes[1] -eq 0xBB -and $Bytes[2] -eq 0xBF) {
        return [Text.UTF8Encoding]::new($true)
    }

    if ($Bytes.Length -ge 2 -and $Bytes[0] -eq 0xFF -and $Bytes[1] -eq 0xFE) {
        return [Text.UnicodeEncoding]::new($false, $true)
    }

    if ($Bytes.Length -ge 2 -and $Bytes[0] -eq 0xFE -and $Bytes[1] -eq 0xFF) {
        return [Text.UnicodeEncoding]::new($true, $true)
    }

    if ($Bytes.Length -ge 4 -and $Bytes[0] -eq 0xFF -and $Bytes[1] -eq 0xFE -and $Bytes[2] -eq 0x00 -and $Bytes[3] -eq 0x00) {
        return [Text.UTF32Encoding]::new($false, $true)
    }

    if ($Bytes.Length -ge 4 -and $Bytes[0] -eq 0x00 -and $Bytes[1] -eq 0x00 -and $Bytes[2] -eq 0xFE -and $Bytes[3] -eq 0xFF) {
        return [Text.UTF32Encoding]::new($true, $true)
    }

    return [Text.UTF8Encoding]::new($false)
}

function Normalize-Content {
    param(
        [string]$Content,
        [string]$TargetEol
    )

    $newline = if ($TargetEol -eq 'crlf') { "`r`n" } else { "`n" }
    $normalized = $Content -replace "`r`n", "`n"
    $normalized = $normalized -replace "`r", "`n"

    if ($normalized.Length -eq 0) {
        return $normalized
    }

    $normalized = $normalized.TrimEnd("`n")
    $normalized = $normalized -replace "`n", $newline
    return $normalized + $newline
}

if (-not (Test-Path (Join-Path $RepoRoot '.git'))) {
    throw "RepoRoot '$RepoRoot' neobsahuje .git adresar."
}

$files = Get-GitFiles -Root $RepoRoot
$updated = New-Object System.Collections.Generic.List[string]
$skippedBinary = New-Object System.Collections.Generic.List[string]

foreach ($relativePath in $files) {
    $fullPath = Join-Path $RepoRoot $relativePath

    if (-not (Test-Path $fullPath -PathType Leaf)) {
        continue
    }

    $bytes = [IO.File]::ReadAllBytes($fullPath)
    if ($bytes.Length -eq 0) {
        continue
    }

    if (Test-IsBinary -Bytes $bytes) {
        $skippedBinary.Add($relativePath) | Out-Null
        continue
    }

    $encoding = Get-FileEncoding -Bytes $bytes
    $content = $encoding.GetString($bytes)
    $targetEol = Get-TargetLineEnding -Root $RepoRoot -RelativePath $relativePath
    $normalized = Normalize-Content -Content $content -TargetEol $targetEol

    if ($normalized -ceq $content) {
        continue
    }

    $updated.Add($relativePath) | Out-Null

    if (-not $WhatIf) {
        [IO.File]::WriteAllText($fullPath, $normalized, $encoding)
    }
}

if ($WhatIf) {
    Write-Host 'Dry run: soubory urcene k uprave line endings:'
} else {
    Write-Host 'Upravene soubory:'
}

if ($updated.Count -eq 0) {
    Write-Host '  zadne'
} else {
    $updated | Sort-Object | ForEach-Object { Write-Host "  $_" }
}

Write-Host ''
Write-Host ("Preskocene binarni soubory: {0}" -f $skippedBinary.Count)
