#!/usr/bin/env bash

set -e

rm -f generated-drop-schema.sql
rm -f generated-create-schema.sql

cd ../..

echo "press CTRL-C to stop server after the application has started successfully"
mvn spring-boot:run -Dspring.profiles.active=postgresql \
  -Dspring.jpa.hibernate.ddl-auto=create \
  -Dspring.jpa.properties.javax.persistence.schema-generation.create-source=metadata \
  -Dspring.jpa.properties.javax.persistence.schema-generation.drop-source=metadata \
  -Dspring.jpa.properties.javax.persistence.schema-generation.scripts.action=create \
  -Dspring.jpa.properties.javax.persistence.schema-generation.scripts.create-target=etc/schema1/generated-create-schema.sql
