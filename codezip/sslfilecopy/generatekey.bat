del filecopykeystore.jks

keytool -genkey -alias filecopy -keyalg RSA  -keystore filecopykeystore.jks -storepass password -keypass password -validity 1000 -keysize 2048 --dname "CN=filecopy,OU=filecopy,O=gt.com,L=bangalore,C=IN"

keytool -list -keystore filecopykeystore.jks -storepass password

