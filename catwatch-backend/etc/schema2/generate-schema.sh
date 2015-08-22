#!/usr/bin/env bash

set -e

pg_dump --username=postgres --host=localhost --file=create_catwatch.sql --schema-only --dbname=catwatch