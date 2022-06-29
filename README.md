<h1 align="center">
  Spring Blog App
</h1>

<p align="center">
  <a href="#routes">Routes</a>&nbsp;&nbsp;&nbsp;|&nbsp;&nbsp;&nbsp;
  <a href="#documentation">Documentation</a>&nbsp;&nbsp;&nbsp;|&nbsp;&nbsp;&nbsp;
  <a href="#coverage">Coverage</a>&nbsp;&nbsp;&nbsp;|&nbsp;&nbsp;&nbsp;
  <a href="#technologies">Technologies</a>&nbsp;&nbsp;&nbsp;|&nbsp;&nbsp;&nbsp;
  <a href="#building">Building</a>&nbsp;&nbsp;&nbsp;|&nbsp;&nbsp;&nbsp;
  <a href="#contributing">Contributing</a>&nbsp;&nbsp;&nbsp;
</p>


## Routes
| Method | URL                                                                 | Description                    |
|--------|---------------------------------------------------------------------|--------------------------------|
| POST   | http://localhost:8080/api/v1/auth/login                             | Login                          |
| POST   | http://localhost:8080/api/v1/users                                  | Sign Up                        |
| GET    | http://localhost:8080/api/v1/users/email-verification?token={token} | Verify E-mail Account          |
| POST   | http://localhost:8080/api/v1/users/reset-password-request           | Password Reset Request         |
| POST   | http://localhost:8080/api/v1/users/reset-password?token={token}     | Password Reset Update          |
| GET    | http://localhost:8080/api/v1/users/                                 | Get All Users                  |
| GET    | http://localhost:8080/api/v1/users/{userID}                         | Get Specific User              |
| GET    | http://localhost:8080/api/v1/users/{userID}/addresses               | Get All Addresses Belongs User |
| GET    | http://localhost:8080/api/v1/users/{userID}/addresses/{addressID}   | Get Specific Address From User |
| PUT    | http://localhost:8080/api/v1/users/{userID}                         | Update User Data               |
| DELETE | http://localhost:8080/api/v1/users/{userID}                         | Delete User                    |


## Documentation
| URL                                         | Description          |
|---------------------------------------------|----------------------|
| http://localhost:8080/swagger-ui/index.html | Visual Documentation |
| http://localhost:8080/v2/api-docs           | Api Documentation    |

<img src="p2.jpg">


## Coverage
<img src="p1.jpg">

## Technologies
This project was developed using the following technologies:
- [Spring Boot](https://spring.io/)
- [Spring Security](https://spring.io/)
- [Spring Data JPA](https://spring.io/projects/spring-data-jpa)
- [Spring Hateoas](https://spring.io/projects/spring-hateoas)
- [Lombok](https://projectlombok.org/)
- [JUnit 5](https://junit.org/junit5/)
- [Model Mapper](http://modelmapper.org/)
- [JWT](https://jwt.io/)
- [Amazon SES](https://docs.aws.amazon.com/sdk-for-java/latest/developer-guide/get-started.html)
- [MYSQL + H2]()


## Building
You'll need [Java 11+](https://www.oracle.com/br/java/technologies/javase-jdk11-downloads.html) and [Maven](https://maven.apache.org/download.cgi) installed on your computer in order to build this app.

```bash
$ git clone https://github.com/eric-souzams/spring-blog-app.git
$ cd spring-blog-app
$ mvn spring-boot:run
```


## Contributing
This repository is currently under development. If you want to contribute please fork the repository and get your hands dirty, and make the changes as you'd like and submit the Pull request.