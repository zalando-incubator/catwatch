FROM zalando/openjdk:8u45-b14-2

COPY ../catwatch-backend/target/catwatch-backend.jar /catwatch-backend.jar

COPY scm-source.json /scm-source.json

RUN apt-get install -y postgresql-client

CMD java -jar /catwatch-backend.jar

