@echo off

REM Clean previous folder
rd /s /q build
mkdir build

REM Package the project into a .jar
cd Server/out/production/Server
jar -cvf all.jar *
move all.jar ../../../../build/all.jar
move background.wav ../background.wav
jar -cvf nobg.jar *
move nobg.jar ../../../../build/nobg.jar
cd ..
move background.wav Server/background.wav
cd ../../..

REM Build a custom JRE runtime
REM jdeps --list-deps Client.jar
REM jdeps --list-deps Server.jar
REM java.base
REM java.desktop
REM java.prefs
cd build
jlink --no-header-files --no-man-pages --compress=2 --strip-debug --add-modules java.base,java.desktop,java.prefs --output java-runtime

REM Build the entire thing
echo cd java-runtime/bin >> startServer.bat
echo java.exe -cp all.jar GameServer.GameServer >> startServer.bat
echo cd java-runtime/bin >> startClient.bat
echo java.exe -cp all.jar Client.ClientGUI >> startClient.bat
move all.jar java-runtime/bin/all.jar
tar -acf all-build.zip java-runtime startServer.bat startClient.bat

REM Build the nobg build
del "java-runtime\bin\all.jar"
del startServer.bat
del startClient.bat
echo cd java-runtime/bin >> startServer.bat
echo java.exe -cp nobg.jar GameServer.GameServer >> startServer.bat
echo cd java-runtime/bin >> startClient.bat
echo java.exe -cp nobg.jar Client.ClientGUI >> startClient.bat
move nobg.jar java-runtime/bin/nobg.jar
tar -acf nobg-build.zip java-runtime startServer.bat startClient.bat

REM Cleanup
del startServer.bat
del startClient.bat
rd /s /q java-runtime

echo "Done"
pause