#!/bin/bash
REV=$(git rev-parse HEAD)
URL=$(git config --get remote.origin.url)
STATUS=$(git status --porcelain)
if [ -n "$STATUS" ]; then
    REV="$REV (locally modified)"
fi
# finally write hand-crafted JSON to scm-source.json
echo '{"url": "git:'$URL'", "revision": "'$REV'", "author": "'$USER'", "status": "'$STATUS'"}' > target/generated-sources/scm-source.json

