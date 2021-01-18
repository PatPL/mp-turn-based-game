rd /s /q build
mkdir build
cd Server/out/production/Server
jar -cvfe Server.jar GameServer.GameServer *
move Server.jar ../../../../build/Server.jar
jar -cvfe Client.jar Client.ClientGUI *
move Client.jar ../../../../build/Client.jar
cd ../../../..
echo "Done"
pause