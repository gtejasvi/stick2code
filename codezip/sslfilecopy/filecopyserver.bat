set JAR_PATH=./lib
set CLASSPATH=.;%JAR_PATH%/filecopy-0.0.1-SNAPSHOT.jar;%JAR_PATH%/slf4j-api-1.7.5.jar;%JAR_PATH%/slf4j-log4j12-1.7.5.jar;%JAR_PATH%/log4j-1.2.15.jar
set port=%1

rem java -Djavax.net.debug=ssl,handshake -Djavax.net.ssl.keyStore=./filecopykeystore.jks  -Djavax.net.ssl.keyStorePassword=password -cp %CLASSPATH% com.gt.stick2code.filecopy.server.FileCopySocketServer -s %port%

java -Djavax.net.debug=ssl,handshake -Djavax.net.ssl.keyStore=./filecopykeystore.jks  -Djavax.net.ssl.keyStorePassword=password -cp %CLASSPATH% com.gt.stick2code.filecopy.server.FileCopySocketServer -s %port%

pause