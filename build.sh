#/bin/sh

set -e

if [ "$BUILD_TOOL" = "maven" ]; then
	
	mvn clean install

else

    cd catwatch-backend
    chmod +x gradlew
	./gradlew clean build
	cd .. 

fi


