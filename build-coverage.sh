#!/usr/bin/env bash
set -e

./mvnw clean integration-test jacoco:report coveralls:report -pl catwatch-backend -Pcoverage
