#/bin/sh

set -e

if [ "$BUILD_TOOL" = "maven" ]; then
	
	mvn clean test jacoco:report coveralls:report -pl catwatch-backend -Pcoverage

else

    # nothing to do

fi