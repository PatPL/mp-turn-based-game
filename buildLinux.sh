#!/bin/bash

# Force the script to be run as root
# if [ $EUID -ne 0 ]; then
#     echo "  Run this script as root"
#     echo "  >sudo $0"
#     echo "  "
#     echo "  Aborting..."
#     exit
# fi

exec_start=$(date +%s%N)

# Clean previous folder
rm -rf buildLinux
mkdir buildLinux

# Package the project into a .jar
cd Server/out/production/Server
echo "  Packaging the project into a jar file..."
jar -cvf all.jar * > /dev/null
mv all.jar ../../../../buildLinux
cd ../../../..

# Build a custom JRE runtime
# jdeps --list-deps all.jar
# java.base
# java.desktop
# java.prefs
cd buildLinux
echo "  Building a custom JRE runtime..."
jlink --no-header-files --no-man-pages --compress=2 --strip-debug --add-modules java.base,java.desktop,java.prefs --output java-runtime
# Debian somehow causes jlink to output object files with debug symbols
# https://github.com/docker-library/openjdk/issues/217
# https://bugs.debian.org/cgi-bin/bugreport.cgi?bug=905575
# That makes the runtime huge (~320MB)
# Strip debug symbols off of every *.so file
# That reduces the size to ~36MB, in line with the Windows counterpart
echo "  Stripping debug symbols from the JRE runtime..."
find java-runtime -name '*.so' | xargs -I '{}' strip --strip-debug {}

# Add launchers and package the project
echo "  Creating launcher scripts..."
cp ../buildResources/startServer.sh ./
cp ../buildResources/startServerGUI.sh ./
cp ../buildResources/startClient.sh ./

echo "  Compressing everything into a 7z archive..."
mv all.jar java-runtime/bin
7z a -bso0 -bsp0 all-build-Linux java-runtime startServer.sh startServerGUI.sh startClient.sh

# Cleanup
echo "  Final cleanup..."
rm -rf startServer.sh
rm -rf startServerGUI.sh
rm -rf startClient.sh
rm -rf java-runtime

exec_time=$((($(date +%s%N) - exec_start) / 1000000))
printf "  Done in $((exec_time / 1000))."
printf "%03ds\n" $((exec_time % 1000))
