set JAR_PATH=./lib
set CLASSPATH=.;%JAR_PATH%/filecopy.jar;%JAR_PATH%/slf4j-api-1.7.5.jar;%JAR_PATH%/slf4j-log4j12-1.7.5.jar;%JAR_PATH%/log4j-1.2.15.jar

java -cp %CLASSPATH% com.gt.stick2code.filecopy.client.PutFileSocketClient -ro 16.185.97.24 50000 "D:\temp\filecopy\source" "C:\users\bankapum\desktop\filecopy\data" 3

pause