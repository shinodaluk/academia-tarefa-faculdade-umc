# Compila o pacote model e os testes, depois roda a suíte com o JUnit Console.
$ErrorActionPreference = 'Continue'
Set-Location $PSScriptRoot

$saida = 'build/testes'
$junit = 'lib/junit-platform-console-standalone-1.11.4.jar'
$gson = 'src/java/docs/jar/gson-2.10.1.jar'

if (Test-Path $saida) { Remove-Item -Recurse -Force $saida }
New-Item -ItemType Directory -Force "$saida/main", "$saida/test" | Out-Null

$fontes = Get-ChildItem src/java/model -Filter *.java | ForEach-Object FullName
javac -encoding UTF-8 -cp $gson -d "$saida/main" $fontes
if ($LASTEXITCODE -ne 0) { exit $LASTEXITCODE }

$testes = Get-ChildItem test -Recurse -Filter *Test.java | ForEach-Object FullName
javac -encoding UTF-8 -cp "$saida/main;$junit;$gson" -d "$saida/test" $testes
if ($LASTEXITCODE -ne 0) { exit $LASTEXITCODE }

java -jar $junit execute --class-path "$saida/main;$saida/test;$gson" --scan-class-path "$saida/test" --details=tree
exit $LASTEXITCODE
