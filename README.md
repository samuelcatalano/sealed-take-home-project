# Sealed
### Sealed Take Home Project

## Tech Stack
| Technology | Version |
|--|--|
| **Java** | 17.0.1 2021-10-19 |
| **Spring Boot** | 3.0.2.RELEASE |
| **Spring JPA** | 3.0.2.RELEASE |
| **Project Lombok** | 1.18.24 |
| **Jupiter JUnit** | 3.0.2.RELEASE |
| **Mockito** | 4.8.1 |
| **H2 Memory** | 2.1.212 |
| **Liquibase** | 4.19.0 |
| **Springdoc OpenAPI Swagger** | 2.0.2 |

## Database
As soon as you start the **internal** application, the liquibase _changelog.yml_  file will create the tables automatically.

#### Access H2 Memory Database:

Once with the application running you can access: http://localhost:8080/h2-console

jdbc: `jdbc:h2:mem:db;DB_CLOSE_DELAY=-1;DB_CLOSE_ON_EXIT=FALSE;AUTO_RECONNECT=TRUE;DATABASE_TO_UPPER=false;`  
user: _admin_  
pass: _admin_

## Swagger Open Rest API
- http://localhost:8081/swagger-ui.html

## Running the application:
> IDE (IntelliJ, Eclipse, NetBeans):
- Importing the project as Maven project on your favourite IDE.
- Build project using Java 17
- Run/Debug project from Main Application Class :: `SealedTechChallengeApplication.class`

> Terminal:
- `mvn spring-boot:run`

## Running the tests
> Terminal:
- `mvn test`

## GraalVM Native Support

This project has been configured to let you generate either a lightweight container or a native executable.
It is also possible to run your tests in a native image.

### Lightweight Container with Cloud Native Buildpacks

If you're already familiar with Spring Boot container images support, this is the easiest way to get started.
Docker should be installed and configured on your machine prior to creating the image.

To create the image, run the following goal:

```
$ ./mvnw spring-boot:build-image -Pnative
```

Then, you can run the app like any other container:

```
$ docker run --rm -p 8080:8080 sealed-tech-challenge:0.0.1-SNAPSHOT
```

## APIs

> **POST**  
Create a new parking lot:  
http://localhost:8080/api/parking-lot

> json example:
```javascript
{
    "name": "Central Parking",
    "capacity": 35,
    "motorcycleSpots": 10,
    "carSpots": 20,
    "vanSpots": 5,
    "spots": [
        {
            "type": "MOTORCYCLE",
            "occupied": false
        },
        {
            "type": "MOTORCYCLE",
            "occupied": false
        },
        {
            "type": "MOTORCYCLE",
            "occupied": false
        },
        {
            "type": "MOTORCYCLE",
            "occupied": false
        },
        {
            "type": "MOTORCYCLE",
            "occupied": false
        },
        {
            "type": "MOTORCYCLE",
            "occupied": false
        },
        {
            "type": "MOTORCYCLE",
            "occupied": false
        },
        {
            "type": "MOTORCYCLE",
            "occupied": false
        },
        {
            "type": "MOTORCYCLE",
            "occupied": false
        },
        {
            "type": "MOTORCYCLE",
            "occupied": false
        },
        {
            "type": "CAR",
            "occupied": false
        },
        {
            "type": "CAR",
            "occupied": false
        },
        {
            "type": "CAR",
            "occupied": false
        },
        {
            "type": "CAR",
            "occupied": false
        },
        {
            "type": "CAR",
            "occupied": false
        },
        {
            "type": "CAR",
            "occupied": false
        },
        {
            "type": "CAR",
            "occupied": false
        },
        {
            "type": "CAR",
            "occupied": false
        },
        {
            "type": "CAR",
            "occupied": false
        },
        {
            "type": "CAR",
            "occupied": false
        },
        {
            "type": "CAR",
            "occupied": false
        },
        {
            "type": "CAR",
            "occupied": false
        },
        {
            "type": "CAR",
            "occupied": false
        },
        {
            "type": "CAR",
            "occupied": false
        },
        {
            "type": "CAR",
            "occupied": false
        },
        {
            "type": "CAR",
            "occupied": false
        },
        {
            "type": "CAR",
            "occupied": false
        },
        {
            "type": "CAR",
            "occupied": false
        },
        {
            "type": "CAR",
            "occupied": false
        },
        {
            "type": "CAR",
            "occupied": false
        },

        {
            "type": "VAN",
            "occupied": false
        },
        {
            "type": "VAN",
            "occupied": false
        },
        {
            "type": "VAN",
            "occupied": false
        },
        {
            "type": "VAN",
            "occupied": false
        },
        {
            "type": "VAN",
            "occupied": false
        }        
    ]
}
```

> **POST**  
Park a vehicle:  
http://localhost:8080/api/parking-lot/park-vehicle

> json example:
```javascript
{
    "type": "CAR",
    "licensePlate": "ABC1234"
}
```
> **POST**  
Unpark a vehicle:  
http://localhost:8080/api/parking-lot/unpark-vehicle/ABC1234

> **GET**  
Retrieve an existing parking lot:  
http://localhost:8080/api/parking-lot/1

> **GET**  
Verify if parking lot is full:  
http://localhost:8080/api/parking-lot/is-full

> **GET**  
Retrieve the available spots:  
http://localhost:8080/api/parking-lot/available-spots

> **GET**  
Retrieve the occupied spots:  
http://localhost:8080/api/parking-lot/occupied-spots

> **GET**  
Retrieve how many spots vans are taking up:  
http://localhost:8080/api/parking-lot/van-spots

## Made Decisions
1) I have chosen to use Spring Boot because I think configuration, annotations and flows are newer to me. I started using Micronaut but faced some issues probably because I should spend more time reading the documentation but I don't want to take the risk to lose the deadline to deliver the take-home.
2) I have chosen to keep separate enums for CarType and SpotType even though the values are the same at this moment. I made this decision because in the future we can add new vehicles types but keep the same spot types and just work with the adjacent spots.
3) When we try to park a van, we first try to occupy the spots designated for vans and only then occupy the spots designated for cars. When a van stops in a car spot, first we check if there are at least 3 spots available and we mark the 3 as occupied but just set one with the vehicle itself.


## Possible Improvements

- Integrating spring-security for enhanced security measures.
- Establishing a CI/CD pipeline to automate deployment and enable seamless integrations.
- Not deploying the app on a server due to limitations with Heroku and the use of free credits on AWS account. However, the ability to explain the deployment process in a technical interview would be a valuable asset.
