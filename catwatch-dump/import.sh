#!/bin/bash

set -e

importurl=http://ec2-52-28-130-84.eu-central-1.compute.amazonaws.com/import

echo "file $1 will be imported to $importurl ..."
# I guess it won't work with UTF-8
curl -H "Content-Type: application/json; charset=ISO-8859-1" -X POST -d @$1 $importurl

echo ""
echo "import finished"

