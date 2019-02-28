# EjazdyBackend

Backend v Spring Boot  for semestral thesis eJazdy. Frontend and 
instructions how to run it can be found in this repo [ejazdy-frontend](https://github.com/spacive/ejazdy-frontend).

## Description of the semestral thesis

eJazdy is a registration system of rides for car driving school. In the system, there are three
roles of users: Administrator, Instructor and Student. A basic principle of the system lies in 
the ability of instructor to create and publish dates of rides, which students are allowed to register on.
Removal of the registered student to the ride is allowed to Administrators and Instructors without restrictions.
Students are allowed to cancel their registration no later than 24 hours before the lesson starts. The most important
role of the Administrator is user management - create/delete student or instructor from system. Creation of the new user
is accomplished by email invitation which is sent with auto-generated password. Invited user is required to fill the personal 
data and change the password when it is logged in the first time. Administrator is also allowed to register existing
students to lessons, which were published by Instructor.

### Utilized technologies
- Spring Boot
- Amazon Cognito
- Amazon DynamoDBs
- Angular5

### Docs

Documentation in can be found here[docs](https://github.com/spacive/ejazdy-backend/tree/master/docs).

## Build a execution

Requirements: maven

1. Clone the repo
```
git clone [repo_url]
cd ejazdy-backend
```

2. Configure AWS credentials

eJazdy uses services from Amazon - Cognito and DynamoDB. To access these services, 
the backend needs AWS credentials. Configuration of these credentials is possible in a two ways:
- via enviroment variables, in a console:
```
export AWS_ACCESS_KEY=[key]
export AWS_SECRET_KEY=[secret]
```
- or directly in a config file application.properties
```
cognito.access-key=[key]
cognito.secret-key=[secret]
dynamo.access-key=[key]
dynamo.secret-key=[secret]
```

3. Build
```
mvn package
```

4. Execution
```
java -jar target/ejazdy-backend-0.0.1-SNAPSHOT.jar
```
Backend should run by default on **localhost:8090**

## Autor
Juraj Haluška (https://github.com/spacive)
