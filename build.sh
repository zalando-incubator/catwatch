#/bin/sh

set -e

if [ "$BUILD_TOOL" = "maven" ]; then
	
	mvn clean install

else

    cd catwatch-backend
	./gradlew clean build
	cd .. 

fi


