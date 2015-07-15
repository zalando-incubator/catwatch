FROM zalando/openjdk:8u45-b14-2

RUN apt-get update && apt-get install -y apache2

RUN mkdir -p /var/www/catwatch/html

COPY html /var/www/catwatch/html

RUN mkdir /etc/apache2/auth

COPY auth /etc/apache2/auth

COPY apache2 /etc/apache2

COPY sites-available /etc/apache2/sites-available

COPY scm-source.json /scm-source.json

RUN service apache2 start && a2dissite 000-default.conf && a2ensite catwatch.conf

RUN rm -rf /var/www/html

CMD service apache2 start && tail -f /dev/null


