$ErrorActionPreference = 'Stop'
$repoRoot = (Resolve-Path (Join-Path $PSScriptRoot '../../..')).Path
$baseline = Join-Path $PSScriptRoot 'baseline-b416df2'
$classes = Join-Path $repoRoot 'target/r7-baseline-classes'
$caseInputs = [ordered]@{
 'E01-missing-resources' = @('3','1','NOCLIENTE','S001','1','VG001','1','C001','NOVENDEDOR','1','VG001','1','C001','S001','1','NOITEM','0','8')
 'E02-insufficient-stock' = @('3','1','C001','S001','5','VG001','VG001','VG001','VG001','VG001','0','8')
 'E03-invalid-inputs' = @('1','1','R7BADPRICE','Prueba','abc','1','R7BADINT','Prueba','100','abc','0','6','1','R7BADDATE','Prueba','2026-99-99','0','8')
 'E04-duplicate-empty-sale' = @('2','1','C001','Duplicado','3001112233','duplicado@example.com','0','3','1','C001','S001','0','0','8')
 'E10-reload' = @('3','2','0','4','2','0','5','2','0','8')
 'E11-navigation' = @('invalido','1','invalido','0','2','invalido','0','3','invalido','0','4','invalido','0','5','invalido','0','6','invalido','0','7','invalido','0','8')
}
foreach ($caseName in $caseInputs.Keys) {
 $caseRoot=Join-Path $baseline $caseName
 if (Test-Path -LiteralPath $caseRoot) { throw "Evidence already exists: $caseRoot" }
 New-Item -ItemType Directory -Path $caseRoot | Out-Null
 $lines=$caseInputs[$caseName]
 $lines | Set-Content -LiteralPath "$caseRoot/input.txt" -Encoding utf8
 $seed=Join-Path $baseline 'initial-data'
 if ($caseName -eq 'E10-reload') { $seed=Join-Path $baseline 'returns/run-1/data' }
 foreach ($run in 1,2) {
  $runRoot=Join-Path $caseRoot "run-$run"
  New-Item -ItemType Directory -Path $runRoot | Out-Null
  Copy-Item -LiteralPath $seed -Destination "$runRoot/data" -Recurse
  Copy-Item -LiteralPath "$runRoot/data" -Destination "$runRoot/input-data" -Recurse
  Push-Location -LiteralPath $runRoot
  try {
   $lines | & java -cp $classes com.gamezone.Main 1>stdout.txt 2>stderr.txt
   $exitCode=$LASTEXITCODE
   "Exit code: $exitCode" | Set-Content -LiteralPath result.txt -Encoding utf8
  } finally { Pop-Location }
  if ($exitCode -ne 0) { throw "Java failed: $caseName / $run" }
 }
 & git diff --no-index --quiet -- "$caseRoot/run-1/stdout.txt" "$caseRoot/run-2/stdout.txt"
 $outDiff=$LASTEXITCODE
 & git diff --no-index --quiet -- "$caseRoot/run-1/data" "$caseRoot/run-2/data"
 $dataDiff=$LASTEXITCODE
 "stdout comparison: $outDiff`ndata comparison: $dataDiff" | Set-Content -LiteralPath "$caseRoot/comparison.txt" -Encoding utf8
 Write-Output "$caseName : stdout=$outDiff data=$dataDiff"
}