@echo off
echo Iniciando VRS Pedidos GUI...
echo.

REM Verificar se o JAR existe
if not exist "target\vrs-pedidos-gui-1.0.0.jar" (
    echo JAR nao encontrado. Compilando projeto...
    call mvn clean package
    if errorlevel 1 (
        echo Erro na compilacao!
        pause
        exit /b 1
    )
)

echo Executando aplicacao...
java -jar target\vrs-pedidos-gui-1.0.0.jar

pause