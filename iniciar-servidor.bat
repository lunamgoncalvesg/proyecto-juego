@echo off
setlocal
cd /d "%~dp0"

call "%~dp0gradlew.bat" :server:installDist
if errorlevel 1 goto error

call "%~dp0server\build\install\server\bin\server.bat"
pause
exit /b 0

:error
echo No se pudo preparar el servidor. Revisa que Java este instalado y que Gradle termine correctamente.
pause
exit /b 1
