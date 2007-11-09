# To communicate with a secure Fedora, export it's cert from Tomcat:
keytool -export -alias tomcat -keystore server.jks -file server.cer

# and import it into the truststore:
keytool -import -alias YOUR_SERVER_HOST_NAME -keystore truststore -file server.cer
