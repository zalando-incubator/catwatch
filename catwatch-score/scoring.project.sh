#!/usr/bin/env bash

set -e

url=http://ec2-52-28-130-84.eu-central-1.compute.amazonaws.com/config/scoring.project

curl -H "Content-Type: application/json; charset=ISO-8859-1" -X POST -d @scoring.project.js --header "X-Organizations: galanto" $url

