[![Travis build status](https://travis-ci.org/zalando-incubator/catwatch.svg)](https://travis-ci.org/zalando-incubator/catwatch)
[![Coveralls coverage status](https://img.shields.io/coveralls/zalando/catwatch.svg)](https://coveralls.io/r/zalando/catwatch)
[![Apache 2](http://img.shields.io/badge/license-Apache%202-blue.svg)](http://www.apache.org/licenses/LICENSE-2.0)
[![Analytics](https://ga-beacon.appspot.com/UA-65266986-1/zalando/catwatch)](https://github.com/zalando/catwatch)


# CatWatch

CatWatch is a web application that fetches GitHub statistics for your GitHub accounts,
processes and saves your GitHub data in a database, then makes the data available via a REST API.
The data reveals the popularity of your open source projects, most active contributors,
and other interesting points. As an example, you can see the data at work behind the 
[Zalando Open Source page](http://zalando.github.io/).

To compare it to [CoderStats](http://coderstats.net/): CatWatch aggregates your
statistics over a list of GitHub accounts.

## Prerequisites

* Maven 3.0.5
* Java 8
* PostgreSQL 9.4

## Getting Started

First, run PostgreSQL and create the database and a role via a unix shell:

    psql -c "create database catwatch;" -U postgres -h localhost
    psql -c "create database catwatch_test;" -U postgres -h localhost
    psql -c "create user cat1 with password 'cat1';" -U postgres -h localhost

Build and run the web application with Maven.

    cd catwatch-backend

    # build
    ../mvnw package

    # run
    ../mvnw spring-boot:run -Dorganization.list=<listOfGitHubAccounts>

    # run with postgresql and auto create the database (drops existing contents)
    ../mvnw spring-boot:run -Dspring.profiles.active=postgresql -Dspring.jpa.hibernate.ddl-auto=create

    # run with H2 in memory database and auto create the database
    ../mvnw spring-boot:run 
    
    # run with GitHub basic authentication
    ../mvnw spring-boot:run -Dgithub.login=XXX -Dgithub.password=YYY
    
    # run with GitHub OAuth token (supports 2FA)
    ../mvnw spring-boot:run -Dgithub.oauth.token=XXX


The web application is available at http://localhost:8080

It provides the [CatWatch REST API](https://zalando.github.io/catwatch/).

## Details

#### General

Travis CI is used for continuous integration (see button on the top).
Coveralls is used for tracking test coverage (see button on the top).

### Database

By default, the web application uses an H2 in-memory database.
The file application-postgresql.properties demonstrates how a PostgreSQL database can be configured.

After the application is started, some test data are added to the database.

### Admin Console

Currently the scheduler is being executed at 8:00 AM every morning. There are some endpoints.

Initialise the database with test data (for the virtual organization 'galanto''):

    GET /init

Drop the database:

    GET /delete

Import the data (see catwatch-dump/export.txt):

    POST /import

Export the data:

    GET /export

Fetch the data. Please note that the properties ```github.login``` ```github.password``` must be set:

    GET /fetch

Get the config:

    GET /config

Update temporarily the scoring function for projects (see catwatch-score/scoring.project.sh):

    POST /config/scoring.project

### TODO

Here are open tasks regarding the infrastructure:
* Deployment (Database migration, GitHub account credentials management)
* Monitoring
* Robustness (DB fails, CatWatch backend fails)
* Cleaning up the code base

Potential and confirmed bugs:
* not all Zalando projects are listed (confirmed)
* the number of contributors is not correct (potential)
* the time series graphs should be hidden for the first version as they break the responsive layout (confirmed)
