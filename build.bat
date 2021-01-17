rd /s /q build
mkdir build
cd Server/out/production/Server
jar -cvfe Server.jar GameServer.GameServer *
jar -cvfe Client.jar Client.ClientGUI *
move Server.jar ../../../../build/Server.jar
move Client.jar ../../../../build/Client.jar
cd ../../../..
echo "Done"
pause