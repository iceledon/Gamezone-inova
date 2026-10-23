$ErrorActionPreference='Stop'
$repo=(Resolve-Path (Join-Path $PSScriptRoot '../../..')).Path
$baseline=Join-Path $PSScriptRoot 'baseline-b416df2'
$classes=Join-Path $repo 'target/r7-baseline-classes'
$probeClasses=Join-Path $repo 'target/r7-probe-classes'
New-Item -ItemType Directory -Force -Path $probeClasses | Out-Null
& javac --release 17 -encoding UTF-8 -cp $classes -d $probeClasses (Join-Path $PSScriptRoot 'R7BaselineProbe.java')
if($LASTEXITCODE -ne 0){throw 'Probe compilation failed'}
$cases=@('E05-corrupt-startup','E05-write-failure','E06-overlapping-catalog','E07-return-boundary','E08-promotion-tie','E09-legacy-fields')
foreach($case in $cases){
 $root=Join-Path $baseline $case
 # Existing run artifacts are preserved when resuming.
 New-Item -ItemType Directory -Force -Path $root | Out-Null
 foreach($run in 1,2){
  $runRoot=Join-Path $root "run-$run"
  if(Test-Path -LiteralPath "$runRoot/result.txt"){continue}
  New-Item -ItemType Directory -Path $runRoot | Out-Null
  Copy-Item -LiteralPath "$baseline/initial-data" -Destination "$runRoot/data" -Recurse
  $inputs=@('8')
  switch($case){
   'E05-corrupt-startup' { [IO.File]::WriteAllText("$runRoot/data/products.txt",'VIDEOGAME;INVALID') }
   'E06-overlapping-catalog' {
    Add-Content -LiteralPath "$runRoot/data/accessories.csv" -Value 'CONTROLLER,VG001,Overlapping accessory,999.0,5,Wireless,' -Encoding utf8
    $inputs=@('4','2','0','8')
   }
   'E08-promotion-tie' {
    [IO.File]::WriteAllLines("$runRoot/data/promotions.csv",@('PERCENTAGE;R7FIRST;First equal discount;2026-01-01;2027-12-31;10.0','PERCENTAGE;R7SECOND;Second equal discount;2026-01-01;2027-12-31;10.0'))
    $inputs=@('3','1','C001','S001','1','VG001','0','8')
   }
   'E09-legacy-fields' {
    $oldSales=@(Get-Content -LiteralPath "$runRoot/data/sales.txt" | ForEach-Object {($_.Split(';')[0..4])-join ';'})
    $oldReturns=@(Get-Content -LiteralPath "$runRoot/data/returns.csv" | ForEach-Object {($_.Split(';')[0..5])-join ';'})
    [IO.File]::WriteAllLines("$runRoot/data/sales.txt",[string[]]$oldSales)
    [IO.File]::WriteAllLines("$runRoot/data/returns.csv",[string[]]$oldReturns)
    $inputs=@('3','2','0','4','2','0','8')
   }
  }
  Copy-Item -LiteralPath "$runRoot/data" -Destination "$runRoot/input-data" -Recurse
  $inputs | Set-Content -LiteralPath "$runRoot/input.txt" -Encoding utf8
  Push-Location -LiteralPath $runRoot
  try {
   if($case -eq 'E07-return-boundary') { & java -cp "$classes;$probeClasses" R7BaselineProbe dates 1>stdout.txt 2>stderr.txt }
   elseif($case -eq 'E05-write-failure') { & java -cp "$classes;$probeClasses" R7BaselineProbe write 1>stdout.txt 2>stderr.txt }
   else { $inputs | & java -cp $classes com.gamezone.Main 1>stdout.txt 2>stderr.txt }
   $exitCode=$LASTEXITCODE
   "Exit code: $exitCode" | Set-Content -LiteralPath result.txt -Encoding utf8
  } finally {Pop-Location}
  if($case -ne 'E05-corrupt-startup' -and $exitCode -ne 0){throw "Scenario failed: $case"}
 }
 & git diff --no-index --quiet -- "$root/run-1/stdout.txt" "$root/run-2/stdout.txt"
 $outDiff=$LASTEXITCODE
 & git diff --no-index --quiet -- "$root/run-1/data" "$root/run-2/data"
 $dataDiff=$LASTEXITCODE
 "stdout comparison: $outDiff`ndata comparison: $dataDiff" | Set-Content -LiteralPath "$root/comparison.txt" -Encoding utf8
 Write-Output "$case : stdout=$outDiff data=$dataDiff"
 Get-Content -LiteralPath "$root/run-1/stdout.txt" | Select-String 'sistema|failure|memory|age=|First equal|Second equal|Reembolsado|Total:|Monto reembolsado'
}