$ErrorActionPreference='Stop'
$repo=(Resolve-Path (Join-Path $PSScriptRoot '../../..')).Path
$baseline=Join-Path $PSScriptRoot 'baseline-b416df2'
$outputRoot=Join-Path $PSScriptRoot 'replay-five-menus'
$cases=@('products','people','sales','returns','warranties','promotions','accessories','E01-missing-resources','E02-insufficient-stock','E03-invalid-inputs','E04-duplicate-empty-sale','E10-reload','E11-navigation')
foreach($case in $cases){
 $source=Join-Path $baseline $case
 $lines=Get-Content -LiteralPath "$source/input.txt"
 foreach($variant in 'baseline','current'){
  $runRoot=Join-Path $outputRoot "$case/$variant"
  if(Test-Path -LiteralPath $runRoot){throw "Evidence exists: $runRoot"}
  New-Item -ItemType Directory -Force -Path $runRoot | Out-Null
  $seed=Join-Path $source 'run-1/input-data'
  if(!(Test-Path -LiteralPath $seed)){$seed=Join-Path $baseline 'initial-data'}
  Copy-Item -LiteralPath $seed -Destination "$runRoot/data" -Recurse
  $classes=Join-Path $repo "target/r7-$variant-classes"
  Push-Location -LiteralPath $runRoot
  try {$lines | & java -cp $classes com.gamezone.Main 1>stdout.txt 2>stderr.txt;$exitCode=$LASTEXITCODE} finally {Pop-Location}
  "exit=$exitCode" | Set-Content -LiteralPath "$runRoot/result.txt" -Encoding utf8
  if($exitCode -ne 0){throw "Execution failed: $case/$variant"}
 }
 $root=Join-Path $outputRoot $case
 & git diff --no-index --quiet -- "$root/baseline/stdout.txt" "$root/current/stdout.txt"
 $stdoutDiff=$LASTEXITCODE
 & git diff --no-index --quiet -- "$root/baseline/data" "$root/current/data"
 $dataDiff=$LASTEXITCODE
 & git diff --no-index --quiet -- "$root/baseline/stderr.txt" "$root/current/stderr.txt"
 $stderrDiff=$LASTEXITCODE
 "stdoutDiff=$stdoutDiff dataDiff=$dataDiff stderrDiff=$stderrDiff" | Set-Content -LiteralPath "$root/comparison.txt" -Encoding utf8
 Write-Output "$case : stdoutDiff=$stdoutDiff dataDiff=$dataDiff stderrDiff=$stderrDiff"
 if($stdoutDiff -ne 0 -or $dataDiff -ne 0 -or $stderrDiff -ne 0){throw "Regression: $case"}
}