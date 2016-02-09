FROM registry.opensource.zalan.do/stups/openjdk:8u66-b17-1-10

EXPOSE 8080

CMD java $JAVA_OPTS $(java-dynamic-memory-opts) -jar /catwatch-backend.jar

COPY target/catwatch-backend.jar /catwatch-backend.jar

COPY target/generated-sources/scm-source.json /scm-source.json

