@echo off
echo Compilando ProyectoFinal...
javac -encoding UTF-8 -cp "lib\mysql-connector-j-8.3.0.jar" -d bin ^
    src\model\*.java src\db\*.java src\dto\*.java ^
    src\dao\*.java src\service\*.java src\view\*.java src\Main.java

if %errorlevel% neq 0 (
    echo.
    echo ERROR: La compilacion ha fallado. Revisa los errores de arriba.
    pause
    exit /b 1
)

echo Compilacion correcta. Iniciando aplicacion...
java -cp "bin;lib\mysql-connector-j-8.3.0.jar" Main
