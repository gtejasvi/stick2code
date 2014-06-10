echo "Usage :: [alias] [keystore password] [validity in days] [CN : first name and last name] [OU : Organizational Unit] [O : Organization] [L : Location] [C: Two letter country code]
set alias=%1
IF "%alias%" == ""  set alias=sslfilecopy && echo "Defaulting Alias to sslfilecopy" 

set password=%2
IF "%password%" == "" echo "Defaulting keystore password to password" && set password=password

set validity=%3
IF "%validity%" == "" echo "Defaulting validity to 3600" && set validity=3600

set CN=%4
IF "%CN%" == "" echo "Defaulting first name last name to stick2code" && set CN=stick2code

set OU=%5
IF "%OU%" == "" echo "Defaulting organization unit to stick2code" && set OU=stick2code.filecopy
else set OU=%5

set O=%6
IF "%O%" == "" echo "Defaulting Organization to stick2code" && set O=stick2code

set L=%7
IF "%L%" == "" echo "Defaulting Location to bangalore" && set L=bangalore

set C=%8
IF "%C%" == "" echo "Defaulting Country code to IN" && set C=IN

del filecopykeystore.jks

keytool -genkey -alias %alias% -keyalg RSA  -keystore filecopykeystore.jks -storepass %password% -keypass %password% -validity %validity% -keysize 2048 --dname "CN=%CN%,OU=%OU%,O=%O%,L=%L%,C=%C%" 

keytool -list -keystore filecopykeystore.jks -storepass password

pause..