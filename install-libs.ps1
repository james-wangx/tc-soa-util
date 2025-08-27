# Configuration
$libHome = "C:\Program Files\Siemens\TC2412_ROOT\soa_client\java\libs"
$version = "2412"

# Function: convert CamelCase to kebab-case
function Convert-ToKebabCase
{
    param([string]$Name)
    $matches = [regex]::Matches($Name, '[A-Z]+[a-z0-9]*')
    $words = $matches | ForEach-Object { $_.Value.ToLower() }
    return ($words -join '-')
}

# Special jars (manual override)
$specialJars = @(
    [PSCustomObject]@{ File = "$libHome\fccclient.jar"; GroupId = "com.teamcenter.fms"; ArtifactId = "fccclient"; Version = $version },
    [PSCustomObject]@{ File = "$libHome\tcserverjavabinding.jar"; GroupId = "com.teamcenter.net"; ArtifactId = "tcserver"; Version = $version },
    [PSCustomObject]@{ File = "$libHome\TcLogging.jar"; GroupId = "com.teamcenter.logging"; ArtifactId = "tclogging"; Version = $version }
)

# Initialize installation list with special jars
$toInstall = @()

foreach ($jar in $specialJars)
{
    # Check if already installed
    $repoPath = Join-Path $env:USERPROFILE ".m2\repository\$($jar.GroupId.Replace('.', '\') )\$( $jar.ArtifactId )\$( $jar.Version )\$( $jar.ArtifactId )-$( $jar.Version ).jar"
    if (Test-Path $repoPath)
    {
        Write-Host "[EXIST] $( $jar.File ) already installed"
        continue
    }
    $toInstall += $jar
}

# Scan regular jars
$allJars = Get-ChildItem -Path $libHome -Filter 'TcSoa*.jar'

foreach ($file in $allJars)
{
    $base = [System.IO.Path]::GetFileNameWithoutExtension($file.Name)

    # Extract artifactName and version
    $artifactName = $base
    $jarVersion = $version
    if ($base -match '^(?<name>.+?)_(?<ver>[^_]+)$')
    {
        $artifactName = $matches['name']
        $jarVersion = $matches['ver']
    }

    # Skip Rac/Loose
    if ($artifactName -match '(?i)(Rac|Loose)$')
    {
        Write-Host "[SKIP] $( $file.Name ) (suffix Rac/Loose)"
        continue
    }

    # Skip if file already in special jars
    if ($specialJars | Where-Object { $_.File -eq $file.FullName })
    {
        continue
    }

    # Decide groupId
    if ($artifactName -match 'Strong$')
    {
        $groupId = 'com.teamcenter.services'
    }
    else
    {
        $groupId = 'com.teamcenter.soa'
    }

    $artifactId = Convert-ToKebabCase $artifactName

    # Check if already installed
    $repoPath = Join-Path $env:USERPROFILE ".m2\repository\$($groupId.Replace('.', '\') )\$artifactId\$jarVersion\$artifactId-$jarVersion.jar"
    if (Test-Path $repoPath)
    {
        Write-Host "[EXIST] $( $file.Name ) already installed"
        continue
    }

    # Add to installation list
    $toInstall += [PSCustomObject]@{
        File = $file.FullName
        GroupId = $groupId
        ArtifactId = $artifactId
        Version = $jarVersion
    }
}

$total = $toInstall.Count
Write-Host "Total jars to install: $total"

# Install all jars (special + regular)
for ($i = 0; $i -lt $total; $i++) {
    $jar = $toInstall[$i]
    $idx = $i + 1
    $artifact = $jar.ArtifactId
    Write-Host "Installing ${idx}/${total}: ${artifact} ..."

    mvn install:install-file `
        -Dfile="$( $jar.File )" `
        -DgroupId="$( $jar.GroupId )" `
        -DartifactId="$( $jar.ArtifactId )" `
        -Dversion="$( $jar.Version )" `
        -Dpackaging=jar `
        -q 2> $null
}

Write-Host "All installations complete."
