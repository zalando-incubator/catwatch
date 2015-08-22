#!/usr/bin/env bash

set -e

echo "file $1 will be autoformatted to $1.formatted.json ..."
cat $1 | python -m json.tool > $1.formatted.json
echo ""
echo "formatting finished"

