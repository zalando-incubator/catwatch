#!/usr/bin/env bash

set -e

if [ "$BUILD_TOOL" = "maven" ]; then
	
	mvn clean integration-test jacoco:report coveralls:report -pl catwatch-backend -Pcoverage

else

    # nothing to do
	cd .
	
fi