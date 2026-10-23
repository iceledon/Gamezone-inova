$ErrorActionPreference='Stop'
$repo=(Resolve-Path (Join-Path $PSScriptRoot '../../..')).Path
$baseline=Join-Path $PSScriptRoot 'baseline-b416df2'
$outputRoot=Join-Path $PSScriptRoot 'replay-return-date-persistence'
foreach($case in 'returns','warranties','E06-overlapping-catalog','E09-legacy-fields','E10-reload') {
 $source=Join-Path $baseline $case
 $inputFile=Join-Path $source 'input.txt'
 if(!(Test-Path -LiteralPath $inputFile)){$inputFile=Join-Path $source 'run-1/input.txt'}
 $lines=Get-Content -LiteralPath $inputFile
 foreach($variant in 'baseline','current') {
  $runRoot=Join-Path $outputRoot "$case/$variant"
  if(Test-Path -LiteralPath $runRoot){throw "Evidence exists: $runRoot"}
  New-Item -ItemType Directory -Path $runRoot -Force | Out-Null
  Copy-Item -LiteralPath "$source/run-1/input-data" -Destination "$runRoot/data" -Recurse
  $classes=Join-Path $repo "target/r7-$variant-classes"
  Push-Location -LiteralPath $runRoot
  try {$lines | & java -cp $classes com.gamezone.Main 1>stdout.txt 2>stderr.txt; $exitCode=$LASTEXITCODE} finally {Pop-Location}
  "exit=$exitCode" | Set-Content -LiteralPath "$runRoot/result.txt" -Encoding utf8
  if($exitCode -ne 0){throw "Execution failed: $case/$variant"}
 }
 $caseRoot=Join-Path $outputRoot $case
 & git diff --no-index --quiet -- "$caseRoot/baseline/stdout.txt" "$caseRoot/current/stdout.txt"
 $outDiff=$LASTEXITCODE
 & git diff --no-index --quiet -- "$caseRoot/baseline/data" "$caseRoot/current/data"
 $dataDiff=$LASTEXITCODE
 "stdoutDiff=$outDiff dataDiff=$dataDiff" | Set-Content -LiteralPath "$caseRoot/comparison.txt" -Encoding utf8
 Write-Output "$case : stdoutDiff=$outDiff dataDiff=$dataDiff"
 if($outDiff -ne 0 -or $dataDiff -ne 0){throw "Regression: $case"}
}