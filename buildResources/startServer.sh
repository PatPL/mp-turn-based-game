#!/bin/bash
cd java-runtime/bin

if [ -z ${1+x} ]; then
    echo "You can set a custom ip:port like this:"
    echo "  >$0 [ip:port]"
    echo "  >$0 192.168.0.1:10001"
    echo "  "
    ./java -cp all.jar GameServer.GameServer 127.0.0.1:1234
else
    ./java -cp all.jar GameServer.GameServer $1
fi
