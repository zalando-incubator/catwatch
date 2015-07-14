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