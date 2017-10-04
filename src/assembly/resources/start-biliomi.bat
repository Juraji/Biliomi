@ECHO OFF
FOR /F "delims=|" %%I IN ('DIR "biliomi-*.jar" /B /O:D') DO SET LatestVersion=%%I
"%JAVA_HOME%\bin\java.exe" -Dinteractive -Dfile.encoding=UTF-8 -jar "%cd%\%LatestVersion%"