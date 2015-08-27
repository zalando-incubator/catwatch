#!/usr/bin/env bash

set -e

if [ "$BUILD_TOOL" = "maven" ]; then
	
	mvn clean install

else

    cd catwatch-backend
    chmod +x gradlew
	TERM=dumb ./gradlew clean build --stacktrace
	cd .. 

fi


