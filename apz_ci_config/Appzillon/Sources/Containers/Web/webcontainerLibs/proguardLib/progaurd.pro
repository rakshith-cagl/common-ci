-verbose

-injars ../../target/appzillonplugins.jar
-outjars ../../src/main/webapp/WEB-INF/lib

# Before Java 9, the runtime classes were packaged in a single jar file.
#-libraryjars <java.home>/lib/rt.jar
#-libraryjars <java.home>/lib/jce.jar

# As of Java 9, the runtime classes are packaged in modular jmod files.
-libraryjars <java.home>/jmods/java.base.jmod(!**.jar;!module-info.class)
-libraryjars <java.home>/jmods/java.sql.jmod (!**.jar;!module-info.class)

-libraryjars <user.home>/.m2/repository/commons-codec/commons-codec/1.15/commons-codec-1.15.jar
-libraryjars <user.home>/.m2/repository/javax/servlet/javax.servlet-api/3.0.1/javax.servlet-api-3.0.1.jar
-libraryjars <user.home>/.m2/repository/commons-io/commons-io/2.11.0/commons-io-2.11.0.jar
-libraryjars <user.home>/.m2/repository/org/slf4j/slf4j-api/1.7.32/slf4j-api-1.7.32.jar
-libraryjars <user.home>/.m2/repository/ch/qos/logback/logback-core/1.2.11/logback-core-1.2.11.jar
-libraryjars <user.home>/.m2/repository/ch/qos/logback/logback-classic/1.2.11/logback-classic-1.2.11.jar
#-libraryjars <user.home>/.m2/repository/org/apache/logging/log4j/log4j-core/2.17.1/log4j-core-2.17.1.jar
#-libraryjars <user.home>/.m2/repository/org/apache/logging/log4j/log4j-api/2.17.1/log4j-api-2.17.1.jar
#-libraryjars <user.home>/.m2/repository/com/lmax/disruptor/3.4.4/disruptor-3.4.4.jar
#-libraryjars <user.home>/.m2/repository/com/google/code/gson/gson/2.8.5/gson-2.8.5.jar
-libraryjars <user.home>/.m2/repository/org/apache/commons/commons-lang3/3.12.0/commons-lang3-3.12.0.jar
-libraryjars <user.home>/.m2/repository/com/nimbusds/oauth2-oidc-sdk/9.15/oauth2-oidc-sdk-9.15.jar
-libraryjars <user.home>/.m2/repository/com/microsoft/azure/msal4j/1.10.0/msal4j-1.10.0.jar
-libraryjars <user.home>/.m2/repository/com/nimbusds/lang-tag/1.5/lang-tag-1.5.jar
-libraryjars <user.home>/.m2/repository/com/nimbusds/nimbus-jose-jwt/9.12.1/nimbus-jose-jwt-9.12.1.jar
-libraryjars <user.home>/.m2/repository/com/fasterxml/jackson/core/jackson-databind/2.13.3/jackson-databind-2.13.3.jar
#AZUREJARS
-libraryjars <user.home>/.m2/repository/org/springframework/session/spring-session-core/2.6.2/spring-session-core-2.6.2.jar
-libraryjars <user.home>/.m2/repository/org/springframework/spring-web/5.3.29/spring-web-5.3.29.jar
-libraryjars <user.home>/.m2/repository/io/lettuce/lettuce-core/6.1.5.RELEASE/lettuce-core-6.1.5.RELEASE.jar
-libraryjars <user.home>/.m2/repository/biz/paluch/redis/lettuce/3.5.0.Final/lettuce-3.5.0.Final.jar
-libraryjars <user.home>/.m2/repository/org/springframework/session/spring-session-data-redis/2.6.2/spring-session-data-redis-2.6.2.jar
#-libraryjars <user.home>/.m2/repository/com/azure/azure-identity/1.1.3/azure-identity-1.1.3.jar
#-libraryjars <user.home>/.m2/repository/com/azure/azure-security-keyvault-certificates/4.1.2/azure-security-keyvault-certificates-4.1.2.jar
#-libraryjars <user.home>/.m2/repository/com/azure/azure-security-keyvault-secrets/4.2.2/azure-security-keyvault-secrets-4.2.2.jar
#-libraryjars <user.home>/.m2/repository/org/twitter4j/twitter4j-core/4.0.3/twitter4j-core-4.0.3.jar
-libraryjars <user.home>/.m2/repository/org/springframework/spring-expression/5.3.29/spring-expression-5.3.29.jar
-libraryjars <user.home>/.m2/repository/org/springframework/spring-core/5.3.29/spring-core-5.3.29.jar
-libraryjars <user.home>/.m2/repository/io/netty/netty-codec/4.1.95.Final/netty-codec-4.1.95.Final.jar
-libraryjars <user.home>/.m2/repository/org/springframework/spring-context/5.3.29/spring-context-5.3.29.jar
-libraryjars <user.home>/.m2/repository/org/springframework/spring-beans/5.3.29/spring-beans-5.3.29.jar
-libraryjars <user.home>/.m2/repository/org/springframework/spring-aop/5.3.29/spring-aop-5.3.29.jar
-libraryjars <user.home>/.m2/repository/commons-logging/commons-logging/1.2/commons-logging-1.2.jar
-libraryjars <user.home>/.m2/repository/org/apache/httpcomponents/httpmime/4.5.13/httpmime-4.5.13.jar
-libraryjars <user.home>/.m2/repository/org/apache/httpcomponents/httpclient/4.5.13/httpclient-4.5.13.jar
-libraryjars <user.home>/.m2/repository/org/apache/httpcomponents/httpcore/4.4.15/httpcore-4.4.15.jar
-libraryjars <user.home>/.m2/repository/commons-fileupload/commons-fileupload/1.4/commons-fileupload-1.4.jar
-libraryjars <user.home>/.m2/repository/com/sun/jersey/jersey-client/1.18.1/jersey-client-1.18.1.jar
-libraryjars <user.home>/.m2/repository/com/sun/jersey/contribs/jersey-multipart/1.18.1/jersey-multipart-1.18.1.jar
-libraryjars <user.home>/.m2/repository/com/sun/jersey/jersey-core/1.18.1/jersey-core-1.18.1.jar
-libraryjars <user.home>/.m2/repository/com/sun/jersey/jersey-json/1.18.1/jersey-json-1.18.1.jar
-libraryjars <user.home>/.m2/repository/org/codehaus/jettison/jettison/1.1/jettison-1.1.jar
-libraryjars <user.home>/.m2/repository/org/json/json/20090211/json-20090211.jar
-libraryjars <user.home>/.m2/repository/org/bouncycastle/bcprov-jdk14/1.75/bcprov-jdk14-1.75.jar
-libraryjars <user.home>/.m2/repository/org/owasp/encoder/encoder/1.2.3/encoder-1.2.3.jar
-libraryjars <user.home>/.m2/repository/org/apache/commons/commons-text/1.9/commons-text-1.9.jar
-libraryjars <user.home>/.m2/repository/org/owasp/csrfguard/4.1.4/csrfguard-4.1.4.jar
-libraryjars <user.home>/.m2/repository/org/owasp/csrfguard-extension-session/4.1.4/csrfguard-extension-session-4.1.4.jar
-libraryjars <user.home>/.m2/repository/org/keycloak/keycloak-adapter-core/17.0.1/keycloak-adapter-core-17.0.1.jar
-libraryjars <user.home>/.m2/repository/org/keycloak/keycloak-adapter-spi/17.0.1/keycloak-adapter-spi-17.0.1.jar
-libraryjars <user.home>/.m2/repository/org/keycloak/keycloak-common/17.0.1/keycloak-common-17.0.1.jar
-libraryjars <user.home>/.m2/repository/org/keycloak/keycloak-core/17.0.1/keycloak-core-17.0.1.jar
-libraryjars <user.home>/.m2/repository/commons-lang/commons-lang/2.6/commons-lang-2.6.jar
#-libraryjars <user.home>/.m2/repository/org/keycloak/keycloak-tomcat-adapter-spi/17.0.1/keycloak-tomcat-adapter-spi-17.0.1.jar
#-libraryjars <user.home>/.m2/repository/org/keycloak/keycloak-tomcat-core-adapter/17.0.1/keycloak-tomcat-core-adapter-17.0.1.jar
-libraryjars <user.home>/.m2/repository/javax/xml/bind/jaxb-api/2.3.1/jaxb-api-2.3.1.jar


-dontskipnonpubliclibraryclassmembers
-dontshrink
-dontoptimize
-useuniqueclassmembernames
-dontwarn

# Save the obfuscation mapping to a file, so you can de-obfuscate any stack
# traces later on. Keep a fixed source file attribute and all line number
# tables to get line numbers in the stack traces.
# You can comment this out if you're not interested in stack traces.

-printmapping ../Obfuscation.log
-renamesourcefileattribute SourceFile
-keepattributes SourceFile,LineNumberTable

# Preserve all annotations.

-keepattributes *Annotation*

# You can print out the seeds that are matching the keep options below.

#-printseeds out.seeds

# Preserve all public servlets.
-keep public class com.iexceed.webcontainer.startup.WebContextListener
-keep public class com.iexceed.webcontainer.utils.AppzillonRequestFilter
-keep public class com.iexceed.webcontainer.servlet.AppzillonWebContainer
-keep public class com.iexceed.webcontainer.servlet.AppzillonWebContainerHealth
-keep public class com.iexceed.webcontainer.servlet.ReloadProperties

-keep public class com.iexceed.webcontainer.utils.PropertyUtils {
  <methods>; 
}

-keep interface com.iexceed.webcontainer.utils.AppzillonConstants { *; }

-keep interface com.iexceed.appzillon.custom.IRequestProcessor {
    <methods>;
}
-keep public class com.iexceed.appzillon.custom.WebRequestProcessorImpl

-keep public class com.iexceed.webcontainer.logger.**{ 
 <methods>; 
 }

-keep public class com.iexceed.webcontainer.utils.WebProperties{ 
 <methods>; 
 }
 
-keep public class com.iexceed.webcontainer.utils.RSACryptoUtils {
  <methods>; 
}

# Preserve all native method names and the names of their classes.

# Preserve the special static methods that are required in all enumeration
# classes.

-keepclassmembers,allowoptimization enum * {
    public static **[] values();
    public static ** valueOf(java.lang.String);
}

# Explicitly preserve all serialization members. The Serializable interface
# is only a marker interface, so it wouldn't save them.
# You can comment this out if your library doesn't use serialization.
# If your code contains serializable classes that have to be backward
# compatible, please refer to the manual.

-keepclassmembers class * implements java.io.Serializable {
    static final long serialVersionUID;
    static final java.io.ObjectStreamField[] serialPersistentFields;
    private void writeObject(java.io.ObjectOutputStream);
    private void readObject(java.io.ObjectInputStream);
    java.lang.Object writeReplace();
    java.lang.Object readResolve();
}

# Your application may contain more items that need to be preserved;
# typically classes that are dynamically created using Class.forName:

# -keep public class mypackage.MyClass
# -keep public interface mypackage.MyInterface
# -keep public class * implements mypackage.MyInterface
