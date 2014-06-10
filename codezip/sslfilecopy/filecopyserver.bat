set JAR_PATH=./lib
set CLASSPATH=.;%JAR_PATH%/filecopy.jar;%JAR_PATH%/slf4j-api-1.7.5.jar;%JAR_PATH%/slf4j-log4j12-1.7.5.jar;%JAR_PATH%/log4j-1.2.15.jar

java -cp %CLASSPATH% com.gt.stick2code.filecopy.server.FileCopySocketServer

pause