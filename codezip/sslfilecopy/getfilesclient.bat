set JAR_PATH=./lib
set CLASSPATH=.;D:\spring\basicjava\filecopy\target\classes;%JAR_PATH%/filecopy.jar;%JAR_PATH%/slf4j-api-1.7.5.jar;%JAR_PATH%/slf4j-log4j12-1.7.5.jar;%JAR_PATH%/log4j-1.2.15.jar

rem -Djavax.net.debug=all

java -Djavax.net.ssl.trustStore=./filecopykeystoreclient.jks  -Djavax.net.ssl.keyStorePassword=password  -cp %CLASSPATH% com.gt.stick2code.filecopy.client.GetFileSocketClient -rs 127.0.0.1 50000 "D:\temp\sslfilecopy\source" "D:\temp\sslfilecopy\target" 3

pause

