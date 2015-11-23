#!/usr/bin/env bash
set -e
	
mvn clean integration-test jacoco:report coveralls:report -pl catwatch-backend -Pcoverage
