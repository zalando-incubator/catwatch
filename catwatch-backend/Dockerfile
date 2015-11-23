FROM zalando/openjdk:8u66-b17-1-2

COPY target/catwatch-backend.jar /catwatch-backend.jar

COPY target/generated-sources/scm-source.json /scm-source.json

CMD java -jar /catwatch-backend.jar
