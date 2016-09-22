@echo off


echo Hello, we are going to run a sample client. Make sure the TCP monitor is up and running. If not start it now (tools/tcpmon/build/tcpmon.bat)
echo Set,
echo Listen port= 7070 
echo Target port= 8080

pause
set $PATH=$PATH;$JAVA_HOME/bin

Java -cp %AXIS2_HOME%\\lib\*;.; client.MyClient
pause



:END