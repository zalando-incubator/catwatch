[![Travis build status](https://travis-ci.org/zalando/catwatch.svg)](https://travis-ci.org/zalando/catwatch)
[![Coveralls coverage status](https://img.shields.io/coveralls/zalando/catwatch.svg)](https://coveralls.io/r/zalando/catwatch)
[![Apache 2](http://img.shields.io/badge/license-Apache%202-blue.svg)](http://www.apache.org/licenses/LICENSE-2.0)
[![Analytics](https://ga-beacon.appspot.com/UA-65266986-1/zalando/catwatch)](https://github.com/zalando/catwatch)

# catwatch

CatWatch provides a web application that fetches regularly statistics for your GitHub accounts from GitHub.
The web application processes and saves the data in a database and then makes the data available via a REST-API.
The provided data reveal the popularity of your projects, your most active contributors etc.

In comparison to [CoderStats](http://coderstats.net/) the statistics can be aggregated over a list of GitHub accounts.


## Getting started

Build and run the webapp either by Gradle or Maven.

Gradle:

    cd catwatch-backend
    
    # build
    ./gradlew build
    
    # run
    java -jar build/libs/catwatch-backend-0.0.1-SNAPSHOT.jar -Dorganization.list=<listOfGitHubAccounts>


Maven:

    cd catwatch-backend

    # build
    mvn install
    
    # run
    mvn spring-boot:run -Dorganization.list=<listOfGitHubAccounts>


The web application is available under http://localhost:8080

It provides the [CatWatch REST-API](https://zalando.github.io/catwatch/).