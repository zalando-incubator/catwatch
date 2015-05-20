FROM zalando/ubuntu:14.04.1-1

MAINTAINER Joao Santos <joao.santos@zalando.de>
MAINTAINER Jan Löffler <jan.loeffler@zalando.de>

RUN apt-get install python3 python3-pip

ENV ORGANIZATIONS ['zalando', 'zalando-stups']
ENV GITHUB_TOKEN 'Please do never put your github token into repository'

ADD catwatch /catwatch
ADD run_server.py /
ADD requirements.txt /

EXPOSE 8080

CMD pip3 -r requirements.txt
CMD python3 run_server.py