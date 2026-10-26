# AdvancementInfo 1.21.1 NeoForge offline build script
# Usage: powershell -ExecutionPolicy Bypass -File build.ps1
# Depends on local 1.21.1 NeoForge runtime libs (no maven.neoforged.net / Gradle) + JDK 21

$ErrorActionPreference = "Stop"
$javac = "C:\Users\Administrator\AppData\Local\Temp\jdk21\jdk-21.0.2\bin\javac.exe"
$jar   = "C:\Users\Administrator\AppData\Local\Temp\jdk21\jdk-21.0.2\bin\jar.exe"
$proj  = "D:\Users\AdvancementInfo-1.21.1"
$src   = Join-Path $proj "src\main\java"
$res   = Join-Path $proj "src\main\resources"
$out   = Join-Path $proj "build\classes"
$outjar = Join-Path $proj "build\AdvancementInfo-1.21.1-1.4.1.jar"

# Target instance actually runs NeoForge 21.1.250 (fml 4.0.44)
$core = @(
    "D:\.minecraft\libraries\net\neoforged\neoforge\21.1.250\neoforge-21.1.250-universal.jar",
    "D:\.minecraft\libraries\net\neoforged\neoforge\21.1.250\neoforge-21.1.250-client.jar",
    "D:\.minecraft\libraries\net\minecraft\client\1.21.1-20240808.144430\client-1.21.1-20240808.144430-srg.jar",
    "D:\.minecraft\libraries\org\spongepowered\mixin\0.8.5\mixin-0.8.5.jar",
    "D:\.minecraft\libraries\org\slf4j\slf4j-api\2.0.17\slf4j-api-2.0.17.jar"
)
# Runtime library list exported from the version JSON (missing entries are linux/macos natives, irrelevant for compile)
$libs = Get-Content "C:\Users\Administrator\AppData\Local\Temp\gameinfo263\libpaths.txt" | Where-Object { Test-Path $_ }
$cp = ($libs + $core) -join ";"

if (Test-Path $out) { Remove-Item $out -Recurse -Force }
New-Item -ItemType Directory -Force -Path $out | Out-Null

$files = (Get-ChildItem $src -Recurse -Filter "*.java").FullName
# -proc:none: disable the Mixin annotation processor (no refmap needed, mojmap names == runtime names)
& $javac -proc:none -encoding UTF-8 -cp $cp -d $out $files
if ($LASTEXITCODE -ne 0) { throw "Compile failed" }

if (Test-Path $outjar) { Remove-Item $outjar -Force }
& $jar --create --file $outjar -C $out . -C $res .
if ($LASTEXITCODE -ne 0) { throw "Packaging failed" }

Write-Host "Build OK: $outjar ($((Get-Item $outjar).Length) bytes)"
