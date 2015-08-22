[![Travis build status](https://travis-ci.org/zalando/catwatch.svg)](https://travis-ci.org/zalando/catwatch)
[![Coveralls coverage status](https://img.shields.io/coveralls/zalando/catwatch.svg)](https://coveralls.io/r/zalando/catwatch)
[![Apache 2](http://img.shields.io/badge/license-Apache%202-blue.svg)](http://www.apache.org/licenses/LICENSE-2.0)
[![Analytics](https://ga-beacon.appspot.com/UA-65266986-1/zalando/catwatch)](https://github.com/zalando/catwatch)


# CatWatch

CatWatch provides a web application that fetches regularly statistics for your GitHub accounts from GitHub.
The web application processes and saves the data in a database and then makes the data available via a REST-API.
The provided data reveal the popularity of your projects, your most active contributors etc.

In comparison to [CoderStats](http://coderstats.net/) the statistics can be aggregated over a list of GitHub accounts.

## Prerequisites

* Maven 3.0.5
* Java 8
* PostgreSQL 9.4

## Getting started

First run postgresql and create the database and a role via unix shell
    
    psql -c 'create database catwatch;' -U postgres -h localhost
    psql -c "create user cat1 with password 'cat1';" -U postgres -h localhost

Build and run the web application either by Gradle or Maven. 

Gradle:

    cd catwatch-backend
    
    # build
    ./gradlew build
    
    # run
    java -jar build/libs/catwatch-backend-0.0.1-SNAPSHOT.jar -Dorganization.list=<listOfGitHubAccounts> -Dgithub.login=XXX -Dgithub.password=YYY


Maven:

    cd catwatch-backend

    # build
    mvn package
    
    # run
    mvn spring-boot:run -Dorganization.list=<listOfGitHubAccounts>
    
    # run with postgresql and auto create the database (drops existing contents)
    mvn spring-boot:run -Dspring.profiles.active=postgresql -Dspring.jpa.hibernate.ddl-auto=create -Dgithub.login=XXX -Dgithub.password=YYY
    
    # run with H2 in memory database and auto create the database
    mvn spring-boot:run -Dgithub.login=XXX -Dgithub.password=YYY


The web application is available under http://localhost:8080

It provides the [CatWatch REST-API](https://zalando.github.io/catwatch/).

## Details

#### General

Travis CI is used for continuous integration (see button on the top).
Coveralls is used for tracking the test coverage (see button on the top).

### Database

By default the web application uses an H2 in-memory database.
The file application-postgresql.properties demonstrates how a PostgreSQL database can be configured.

After the application is started, some test data are added to the database.

### Admin Console

Currently the scheduler is being executed at 8 each morning. There are some endpoints 

    POST /config/scoring.project
    
Initialise the database
    
    GET /init
    
Drop the database

    GET /delete
    
Import the data

    POST /import
    
Export the data

    GET /export
    
Fetch the data (Please note that the properties ```github.login``` ```github.password``` must be set)
    
    GET /fetch
    
Get the config

    GET /config
    