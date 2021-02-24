@echo off

REM Clean previous folder
rd /s /q buildWindows
mkdir buildWindows

REM Package the project into a .jar
cd Server/out/production/Server
echo   Packaging the project into a jar file...
jar -cvf all.jar *
move all.jar ../../../../buildWindows/all.jar
cd ../../../..

REM Build a custom JRE runtime
REM jdeps --list-deps all.jar
REM java.base
REM java.desktop
REM java.prefs
cd buildWindows
echo   Building a custom JRE runtime...
jlink --no-header-files --no-man-pages --compress=2 --strip-debug --add-modules java.base,java.desktop,java.prefs --output java-runtime

REM Add launchers and package the project
echo   Creating launcher scripts...
echo cd java-runtime/bin >> startServer.bat
echo java.exe -cp all.jar GameServer.GameServer 127.0.0.1:1234 >> startServer.bat
echo cd java-runtime/bin >> startClient.bat
echo java.exe -cp all.jar Client.ClientGUI 127.0.0.1:1234 >> startClient.bat

echo   Compressing everything into a zip archive...
move all.jar java-runtime/bin/all.jar
tar -acf all-build-Windows.zip java-runtime startServer.bat startClient.bat

REM Cleanup
echo   Final cleanup...
del startServer.bat
del startClient.bat
rd /s /q java-runtime

echo "  Done."
pause