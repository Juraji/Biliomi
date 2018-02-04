@echo off
setlocal enableextensions enabledelayedexpansion

path %PATH%%JAVA_HOME%\bin

for /f tokens^=2-3^ delims^=.-_^" %%j in ('java -fullversion 2^>^&1') do set "jver=%%j%%k"
if "%jver%" neq "18" (
    ECHO Java 8 is needed in order to run Biliomi. Please visit https://www.java.com to download and install Java 8.
) else (
    java -jar biliomi.jar %1
)

endlocal
pause