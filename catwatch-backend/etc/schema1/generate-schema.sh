
rm -f generated-drop-schema.sql
rm -f generated-create-schema.sql

cd ../..

echo "press CTRL-D to stop server"
mvn spring-boot:run -Dspring.profiles.active=hbm2ddl -Dspring.jpa.hibernate.ddl-auto=create