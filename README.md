[![Travis build status](https://travis-ci.org/zalando/catwatch.svg)](https://travis-ci.org/zalando/catwatch)
[![Coveralls coverage status](https://img.shields.io/coveralls/zalando/catwatch.svg)](https://coveralls.io/r/zalando/catwatch)
[![Apache 2](http://img.shields.io/badge/license-Apache%202-blue.svg)](http://www.apache.org/licenses/LICENSE-2.0)

# catwatch
CatWatch presents statistics about your most popular projects on github.com and your most active contributors. The results are accessible via REST API and statistics website.

Documentation for the sub modules:

* [catwatch-backend](catwatch-backend/README.md)
* [catwatch-ui-responsive](catwatch-ui-responsive/README.md)


#### Architecture And Stack

General:

* Continuous integration: Travis-CI

Backend:

* Dependency injection: Spring
* Webserver: embedded Tomcat (Spring Boot default)
* Build: Maven and Gradle
* Persistence: H2 via Spring Data JPA