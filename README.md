[![Travis build status](https://travis-ci.org/zalando/catwatch.svg)](https://travis-ci.org/zalando/catwatch)
[![Coveralls coverage status](https://img.shields.io/coveralls/zalando/catwatch.svg)](https://coveralls.io/r/zalando/catwatch)
[![Apache 2](http://img.shields.io/badge/license-Apache%202-blue.svg)](http://www.apache.org/licenses/LICENSE-2.0)
[![Analytics](https://ga-beacon.appspot.com/UA-65266986-1/zalando/catwatch)](https://github.com/zalando/catwatch)

# catwatch

CatWatch presents statistics about your most popular projects on github.com and your most active contributors. The results are accessible via REST API and statistics website.

The project contains a webapp that fetches regularly statistics for selected accounts from GitHub.
The webapp processes and saves the data in a database and then makes it available via a [REST-API](zalando.github.io/catwatch/).

## Getting started

Build and run the webapp either by Gradle or Maven.

Gradle:

    cd catwatch-background
    
    # build
    ./gradlew build
    
    # run
    java -jar build/libs/catwatch-backend-0.0.1-SNAPSHOT.jar -Dorganization.list=<listOfGitHubAccounts>


Maven:

    cd catwatch-background

    # build
    mvn install
    
    # run
    mvn spring-boot:run -Dorganization.list=<listOfGitHubAccounts>


The webapp is available under http://localhost:8080