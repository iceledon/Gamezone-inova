param([string]$OutputDirectory)
$ErrorActionPreference = 'Stop'
$repo = (Resolve-Path (Join-Path $PSScriptRoot '../../..')).Path
$baseline = Join-Path $PSScriptRoot 'baseline-b416df2'
$stamp = Get-Date -Format 'yyyyMMdd-HHmmss'
$build = Join-Path $repo "target/r7-final-$stamp"
if (!$OutputDirectory) { $OutputDirectory = Join-Path $PSScriptRoot "integration-$stamp" }
if (Test-Path -LiteralPath $OutputDirectory) { throw 'Output directory exists; evidence will not be overwritten.' }
New-Item -ItemType Directory -Path $OutputDirectory,$build | Out-Null
$OutputDirectory = (Resolve-Path -LiteralPath $OutputDirectory).Path
Push-Location -LiteralPath $repo
try {
 $head = & git rev-parse HEAD
 $status = & git status --porcelain -- src/main/java
 $diff = & git diff HEAD -- src/main/java
 $diff | Set-Content -LiteralPath "$OutputDirectory/source.diff" -Encoding utf8
 @("HEAD: $head", "Source changes: $status", "Captured: $(Get-Date -Format o)",
   'Status: comparison of current working source, not an assertion of final acceptance.') |
   Set-Content -LiteralPath "$OutputDirectory/reference.txt" -Encoding utf8
 Get-ChildItem src/main/java -Recurse -Filter '*.java' | Get-FileHash -Algorithm SHA256 |
   Select-Object Path,Hash | Export-Csv -LiteralPath "$OutputDirectory/source-hashes.csv" -NoTypeInformation
 & java -version 2> "$OutputDirectory/java-version.txt"
 $originalHashes = @(Get-ChildItem data -Recurse -File | Get-FileHash | Sort-Object Path | ForEach-Object {"$($_.Path)|$($_.Hash)"})
 Expand-Archive -LiteralPath "$baseline/source.zip" -DestinationPath "$build/baseline-source"
 foreach ($variant in 'baseline','current') {
  $sourceRoot = Join-Path $repo 'src/main/java'
  if ($variant -eq 'baseline') { $sourceRoot = Join-Path $build 'baseline-source/src/main/java' }
  $classes = Join-Path $build "$variant-classes"
  $probes = Join-Path $build "$variant-probes"
  New-Item -ItemType Directory -Path $classes,$probes | Out-Null
  $sources = @(Get-ChildItem -LiteralPath $sourceRoot -Recurse -Filter '*.java' | Select-Object -ExpandProperty FullName)
  & javac --release 17 -encoding UTF-8 -d $classes $sources
  if ($LASTEXITCODE -ne 0) { throw "Compilation failed: $variant" }
  & javac --release 17 -encoding UTF-8 -cp $classes -d $probes "$PSScriptRoot/R7BaselineProbe.java"
  if ($LASTEXITCODE -ne 0) { throw "Probe compilation failed: $variant" }
  & javac --release 17 -encoding UTF-8 -cp $classes -d $probes "$PSScriptRoot/RefactorSemanticsProbe.java"
  if ($LASTEXITCODE -ne 0) { throw "Semantics compilation failed: $variant" }
  & java -cp "$classes;$probes" RefactorSemanticsProbe 1> "$OutputDirectory/semantics-$variant.txt" 2> "$OutputDirectory/semantics-$variant-stderr.txt"
  if ($LASTEXITCODE -ne 0) { throw "Semantics execution failed: $variant" }
 }
 $cases = @('products','people','sales','returns','warranties','promotions','accessories',
  'E01-missing-resources','E02-insufficient-stock','E03-invalid-inputs','E04-duplicate-empty-sale',
  'E05-corrupt-startup','E05-write-failure','E06-overlapping-catalog','E07-return-boundary',
  'E08-promotion-tie','E09-legacy-fields','E10-reload','E11-navigation')
 $results = @()
 foreach ($case in $cases) {
  $fixture = Join-Path $baseline $case
  $inputFile = Join-Path $fixture 'input.txt'
  if (!(Test-Path -LiteralPath $inputFile)) { $inputFile = Join-Path $fixture 'run-1/input.txt' }
  $lines = Get-Content -LiteralPath $inputFile
  foreach ($variant in 'baseline','current') {
   $runRoot = Join-Path $OutputDirectory "$case/$variant"
   New-Item -ItemType Directory -Force -Path $runRoot | Out-Null
   $seed = Join-Path $fixture 'run-1/input-data'
   if (!(Test-Path -LiteralPath $seed)) { $seed = Join-Path $baseline 'initial-data' }
   Copy-Item -LiteralPath $seed -Destination "$runRoot/data" -Recurse
   $lines | Set-Content -LiteralPath "$runRoot/input.txt" -Encoding utf8
   $classes = Join-Path $build "$variant-classes"
   $probes = Join-Path $build "$variant-probes"
   Push-Location -LiteralPath $runRoot
   try {
    if ($case -eq 'E07-return-boundary') { & java -cp "$classes;$probes" R7BaselineProbe dates 1>stdout.txt 2>stderr.txt }
    elseif ($case -eq 'E05-write-failure') { & java -cp "$classes;$probes" R7BaselineProbe write 1>stdout.txt 2>stderr.txt }
    else { $lines | & java -cp $classes com.gamezone.Main 1>stdout.txt 2>stderr.txt }
    $exitCode = $LASTEXITCODE
    "Exit code: $exitCode" | Set-Content -LiteralPath result.txt -Encoding utf8
   } finally { Pop-Location }
   $expectedExit = 0
   if ($case -eq 'E05-corrupt-startup') { $expectedExit = 1 }
   if ($exitCode -ne $expectedExit) { throw "Unexpected exit $exitCode in $case/$variant" }
  }
  $caseRoot = Join-Path $OutputDirectory $case
  $comparisons = @{}
  foreach ($name in 'stdout.txt','stderr.txt','data') {
   & git diff --no-index --quiet -- "$caseRoot/baseline/$name" "$caseRoot/current/$name"
   $comparisons[$name] = $LASTEXITCODE
  }
  $passed = ($comparisons.Values | Where-Object {$_ -ne 0}).Count -eq 0
  $results += [pscustomobject]@{Case=$case;StdoutDiff=$comparisons['stdout.txt'];StderrDiff=$comparisons['stderr.txt'];DataDiff=$comparisons['data'];Passed=$passed}
  $results | Export-Csv -LiteralPath "$OutputDirectory/results.csv" -NoTypeInformation
  Write-Output "$case : passed=$passed"
 }
 & git diff --no-index --quiet -- "$OutputDirectory/semantics-baseline.txt" "$OutputDirectory/semantics-current.txt"
 if ($LASTEXITCODE -ne 0) { throw 'Category/refund semantics differ from baseline.' }
 $afterHashes = @(Get-ChildItem data -Recurse -File | Get-FileHash | Sort-Object Path | ForEach-Object {"$($_.Path)|$($_.Hash)"})
 $originalUnchanged = !(Compare-Object $originalHashes $afterHashes)
 "Original data unchanged: $originalUnchanged" | Set-Content -LiteralPath "$OutputDirectory/original-data-check.txt"
 if (!$originalUnchanged) { throw 'Original data changed during verification.' }
 if ($results.Passed -contains $false) { throw "Differences found; inspect $OutputDirectory/results.csv" }
 Write-Output "All recorded comparisons match. Evidence: $OutputDirectory"
 Write-Output 'Review source status and remaining design/build/review obligations before declaring R7 complete.'
} finally { Pop-Location }