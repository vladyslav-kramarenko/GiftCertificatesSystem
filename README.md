# Gift Certificates System REST API

This project serves as a comprehensive solution for both the "REST API Basics" and "REST API Advanced" tasks. It demonstrates the development of a RESTful web service for a Gift Certificates system while adhering to best practices and industry standards. Below, you will find detailed information about the project, its features, and links to the respective tasks.

## Task Descriptions

### [REST API Basics Task](https://github.com/mjc-school/MJC-School/blob/old/stage%20%233/java/module%20%232.%20REST%20API%20Basics/rest_api_basics_task.md)

The "REST API Basics" task provided foundational requirements for building a Gift Certificates system web service. It covered topics such as CRUD operations, many-to-many relationships, API design, and best coding practices.

### [REST API Advanced Task](https://github.com/mjc-school/MJC-School/blob/old/stage%20%233/java/module%20%233.%20REST%20API%20Advanced/rest_api_advanced.md)

The "REST API Advanced" task extended the functionality of the Gift Certificates system, introducing features like pagination, sorting, filtering, and HATEOAS. It also included the use of JPA, Hibernate, and transactions.

### [CI & CD Task](https://github.com/mjc-school/MJC-School/blob/old/stage%20%233/java/module%20%236.%20CI-CD/ci_cd_task.md)

The "CI & CD" task required the configuration of Jenkins for continuous integration and deployment. It included steps such as configuring Jenkins security, integrating with SonarQube, and deploying the application under a local Tomcat server using Jenkins.

## CI/CD Integration

This project includes a Jenkinsfile that automates the CI/CD pipeline as specified in the [CI & CD Task](https://github.com/mjc-school/MJC-School/blob/old/stage%20%233/java/module%20%236.%20CI-CD/ci_cd_task.md). The Jenkins job is responsible for:

- Building the project using the Gradle build script.
- Analyzing the source code with SonarQube to ensure code quality.
- Deploying the application to a local Tomcat server after passing the SonarQube quality gate.

## Project Features

### Core Functionality

- CRUD operations for Gift Certificates, including automatic creation of tags.
- CRD operations for Tags.
- Retrieval of certificates by tag name and partial name/description.
- Sorting certificates by date and name in ascending or descending order.
- Support for creating, updating, and deleting user entities.
- Ability for users to make orders for gift certificates.
- Retrieval of user order information, including cost and timestamp.
- Determining the most widely used tag among users with the highest-cost orders.
- Pagination implemented for all GET endpoints.
- Support for HATEOAS on REST endpoints.

### Technology Stack

- JDK version: 11, utilizing Streams and java.time.
- Spring Framework for web application development.
- PostgreSQL/MySQL database for data storage.
- JUnit and Mockito for testing.
- Spring JDBC Template and JDBC for data access.
- Maven as the build tool for a multi-module project.

### Best Practices

- Clean code adhering to OOD and SOLID principles.
- Clear layered structure with documented public APIs.
- Exception handling with meaningful error messages.
- Abstraction to avoid code duplication.
- Multiple configurations for different environments.
- Utilizes connection pooling for efficient database connections.

## Usage

To use this project, follow these steps:

1. Clone the repository:

   ```sh
   git clone https://github.com/vladyslav-kramarenko/GiftCertificatesSystem.git
   cd GiftCertificatesSystem
2. Build the project using Gradle:
   ./gradlew build

3. Once the build is successful, you can run the application using:
   ./gradlew bootRun
   This will start the application, and it will be accessible at http://localhost:8080.
4. Access the REST API endpoints using a tool like [Postman](https://www.postman.com/). A Postman collection with APIs is provided for easy demonstration.

## Note
Before running the application, make sure you have Java 8 or higher installed on your machine, as well as any other dependencies required for your specific environment.

For CI/CD purposes, you can refer to the Jenkinsfile included in this repository, which automates the build, test, and deployment processes.

This project was created during EPAM Systems external lab internship.

## Conclusion
This project demonstrates a comprehensive solution for the "REST API Basics" and "REST API Advanced" tasks, showcasing best practices, industry standards, and a fully functional Gift Certificates system web service.
