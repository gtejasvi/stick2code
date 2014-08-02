set JAR_PATH=./lib
set CLASSPATH=.;%JAR_PATH%/filecopy-0.0.1-SNAPSHOT.jar;%JAR_PATH%/slf4j-api-1.7.5.jar;%JAR_PATH%/slf4j-log4j12-1.7.5.jar;%JAR_PATH%/log4j-1.2.15.jar;%JAR_PATH%/commons-codec-1.9.jar

set SERVERIP=%1
set SERVERIP=127.0.0.1

set PORT=%2
set PORT=50005

set SRCFOLDER=%3
set SRCFOLDER=".\source"

set TARGET=%4
set TARGET=".\target"

set THREADS=%5
set THREADS=1

java -Djavax.net.ssl.trustStore=./filecopykeystoreclient.jks -Djavax.net.ssl.keyStorePassword=password -cp %CLASSPATH% com.gt.stick2code.filecopy.client.PutFileSocketClient -ros %SERVERIP% %PORT% %SRCFOLDER% %TARGET%  %THREADS%
