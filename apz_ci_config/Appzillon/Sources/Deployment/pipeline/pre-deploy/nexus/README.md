For Nexus  integration:
   
    1. Add the nexus repo authentation details and the nexus m2 repository details in the maven settings.xml and follow the settings.xml which is attached in this folder as a reference.

    2. Update the remote m2 repo for nexus with additional meta-data xml files as below
        
        https://repo.maven.apache.org/maven2/org/eclipse/scout/sdk/deps/org.eclipse.osgi/maven-metadata.xml

        https://repo.maven.apache.org/maven2/org/eclipse/scout/sdk/deps/org.eclipse.equinox.common/maven-metadata.xml

        https://repo.maven.apache.org/maven2/org/eclipse/scout/sdk/deps/org.eclipse.core.jobs/maven-metadata.xml

        https://repo.maven.apache.org/maven2/org/eclipse/scout/sdk/deps/org.eclipse.equinox.registry/maven-metadata.xml

        https://repo.maven.apache.org/maven2/org/eclipse/scout/sdk/deps/org.eclipse.equinox.preferences/maven-metadata.xml

        https://repo.maven.apache.org/maven2/org/eclipse/scout/sdk/deps/org.eclipse.core.contenttype/maven-metadata.xml

        https://repo.maven.apache.org/maven2/org/eclipse/scout/sdk/deps/org.eclipse.core.runtime/maven-metadata.xml

        https://repo.maven.apache.org/maven2/org/eclipse/scout/sdk/deps/org.eclipse.core.expressions/maven-metadata.xml

        https://repo.maven.apache.org/maven2/org/eclipse/scout/sdk/deps/org.eclipse.core.filesystem/maven-metadata.xml


    3. Create Base Images for Appzillon Web and Server

        1. Web

            Create a Docker file with below instructions

                FROM alpine:3.16.0
                RUN apk add --no-cache openjdk17-jre
            
            Build the Web Base Image
                
                docker build -t nexus.apzplatforms.com/apz-base-web:1.0 -f dockerfile .




        2. Server

            Create a Docker file with below instructions

                FROM alpine:3.16.0
                RUN apk add --no-cache openjdk17-jre openssl=1.1.1o-r0

            Build the Server Base Image
                
                docker build -t nexus.apzplatforms.com/apz-base-server:1.0 -f dockerfile .



    3. Push the Base Image to Nexus repo

        docker push nexus.apzplatforms.com/apz-base-web:1.0

        docker push nexus.apzplatforms.com/apz-base-server:1.0
        

    4. Modify the Appzillon Web and Server Docker File with the nexo repo detail in the FROM instruction
        
        FROM nexus.apzplatforms.com/apz-base-server:1.0 // for server

        FROM nexus.apzplatforms.com/apz-base-web:1.0 // for web
