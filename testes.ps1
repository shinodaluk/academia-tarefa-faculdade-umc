# Compila o projeto e os testes, depois roda a suíte com o JUnit Console.
# Funciona no Windows PowerShell e no pwsh do Linux (usado pelo CI).
$ErrorActionPreference = 'Continue'
Set-Location $PSScriptRoot

$sep = [IO.Path]::PathSeparator # ';' no Windows, ':' no Linux
$saida = 'build/testes'
$junit = 'lib/junit-platform-console-standalone-1.11.4.jar'
$libs = @('lib/javax.servlet-api-4.0.1.jar') + (Get-ChildItem src/java/docs/jar -Filter *.jar | ForEach-Object { $_.FullName })
$cp = $libs -join $sep

if (Test-Path $saida) { Remove-Item -Recurse -Force $saida }
New-Item -ItemType Directory -Force "$saida/main", "$saida/test" | Out-Null

$fontes = Get-ChildItem src/java -Recurse -Filter *.java | ForEach-Object FullName
javac --release 21 -encoding UTF-8 -proc:none -cp $cp -d "$saida/main" $fontes
if ($LASTEXITCODE -ne 0) { exit $LASTEXITCODE }

$testes = Get-ChildItem test -Recurse -Filter *.java | ForEach-Object FullName
javac --release 21 -encoding UTF-8 -proc:none -cp (@("$saida/main", $junit, $cp) -join $sep) -d "$saida/test" $testes
if ($LASTEXITCODE -ne 0) { exit $LASTEXITCODE }

java -jar $junit execute --class-path (@("$saida/main", "$saida/test", $cp) -join $sep) --scan-class-path "$saida/test" --details=tree
exit $LASTEXITCODE
