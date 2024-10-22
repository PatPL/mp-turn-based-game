@echo off

REM Clean previous folder
rd /s /q buildWindows
mkdir buildWindows

REM Package the project into a .jar
cd Server/out/production/Server
echo   Packaging the project into a jar file...
jar -cvf all.jar * > nul
move all.jar ../../../../buildWindows/all.jar > nul
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
copy ..\buildResources\startServer.bat . > nul
copy ..\buildResources\startServerGUI.bat . > nul
copy ..\buildResources\startClient.bat . > nul

echo   Compressing everything into a zip archive...
move all.jar java-runtime/bin/all.jar > nul
tar -acf all-build-Windows.zip java-runtime startServer.bat startServerGUI.bat startClient.bat

REM Cleanup
echo   Final cleanup...
del startServer.bat
del startServerGUI.bat
del startClient.bat
rd /s /q java-runtime

echo   Done.
pause