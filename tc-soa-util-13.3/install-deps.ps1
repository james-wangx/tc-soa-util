# ============================================
# Teamcenter SOA Java Dependencies Installer
# ============================================

param(
# Dependencies directory
    [Parameter(Mandatory = $true)]
    [string]$LibHome,

# Maven repository path
    [Parameter(Mandatory = $true)]
    [string]$MavenRepo
)

# ============================================
# Fixed version
# ============================================

$Version = "13000.3.0"

Write-Host "============================================"
Write-Host "Lib Home     : $LibHome"
Write-Host "Maven Repo   : $MavenRepo"
Write-Host "Version      : $Version"
Write-Host "============================================"

# ============================================
# Function: convert CamelCase to kebab-case
# ============================================

function Convert-ToKebabCase
{
    param([string]$Name)

    $matches = [regex]::Matches($Name, '[A-Z]+[a-z0-9]*')
    $words = $matches | ForEach-Object { $_.Value.ToLower() }

    return ($words -join '-')
}

# ============================================
# Function: build maven jar path
# ============================================

function Get-MavenJarPath
{
    param(
        [string]$GroupId,
        [string]$ArtifactId,
        [string]$Version
    )

    $groupPath = $GroupId.Replace('.', '\')

    return Join-Path `
        $MavenRepo `
        "$groupPath\$ArtifactId\$Version\$ArtifactId-$Version.jar"
}

# ============================================
# Special jars
# ============================================

$specialJars = @(
    [PSCustomObject]@{
        File = "$LibHome\fccclient.jar"
        GroupId = "com.teamcenter.fms"
        ArtifactId = "fccclient"
        Version = $Version
    },

    [PSCustomObject]@{
        File = "$LibHome\tcserverjavabinding.jar"
        GroupId = "com.teamcenter.net"
        ArtifactId = "tcserver"
        Version = $Version
    }
)

# ============================================
# Installation list
# ============================================

$toInstall = @()

# ============================================
# Handle special jars
# ============================================

foreach ($jar in $specialJars)
{
    $repoPath = Get-MavenJarPath `
        -GroupId $jar.GroupId `
        -ArtifactId $jar.ArtifactId `
        -Version $jar.Version

    if (Test-Path $repoPath)
    {
        Write-Host "[EXIST] $( $jar.ArtifactId )"
        continue
    }

    $toInstall += $jar
}

# ============================================
# Scan TcSoa jars
# ============================================

$allJars = Get-ChildItem `
    -Path $LibHome `
    -Filter 'TcSoa*.jar'

foreach ($file in $allJars)
{
    $base = [System.IO.Path]::GetFileNameWithoutExtension($file.Name)

    $artifactName = $base
    $jarVersion = $Version

    # Example:
    # TcSoaStrongCore_2506
    if ($base -match '^(?<name>.+?)_(?<ver>[^_]+)$')
    {
        $artifactName = $matches['name']
        $jarVersion = $matches['ver']
    }

    # Skip Rac / Loose
    if ($artifactName -match '(?i)(Rac|Loose)$')
    {
        Write-Host "[SKIP] $( $file.Name )"
        continue
    }

    # Skip special jars
    if ($specialJars | Where-Object { $_.File -eq $file.FullName })
    {
        continue
    }

    # groupId
    if ($artifactName -match 'Strong$')
    {
        $groupId = "com.teamcenter.services"
    }
    else
    {
        $groupId = "com.teamcenter.soa"
    }

    $artifactId = Convert-ToKebabCase $artifactName

    # Maven repo path
    $repoPath = Get-MavenJarPath `
        -GroupId $groupId `
        -ArtifactId $artifactId `
        -Version $jarVersion

    # Already installed
    if (Test-Path $repoPath)
    {
        Write-Host "[EXIST] $artifactId"
        continue
    }

    # Add install task
    $toInstall += [PSCustomObject]@{
        File = $file.FullName
        GroupId = $groupId
        ArtifactId = $artifactId
        Version = $jarVersion
    }
}

# ============================================
# Install jars
# ============================================

$total = $toInstall.Count

Write-Host ""
Write-Host "Total jars to install: $total"
Write-Host ""

for ($i = 0; $i -lt $total; $i++)
{
    $jar = $toInstall[$i]

    $idx = $i + 1

    Write-Host "Installing ${idx}/${total}: $( $jar.ArtifactId )"

    mvn install:install-file `
        -Dfile="$( $jar.File )" `
        -DgroupId="$( $jar.GroupId )" `
        -DartifactId="$( $jar.ArtifactId )" `
        -Dversion="$( $jar.Version )" `
        -Dpackaging=jar `
        -DlocalRepositoryPath="$MavenRepo" `
        -q 2> $null

    if ($LASTEXITCODE -eq 0)
    {
        Write-Host "[SUCCESS] $( $jar.ArtifactId )"
    }
    else
    {
        Write-Host "[FAILED ] $( $jar.ArtifactId )"
    }

    Write-Host ""
}

Write-Host "============================================"
Write-Host "All installations complete."
Write-Host "============================================"
