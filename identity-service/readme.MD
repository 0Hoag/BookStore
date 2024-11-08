# Identity service
This microservice is responsible for:
* Onboarding users
* Roles and permissions
* Authentication

## Tech stack
* Build tool: maven >= 3.9.5
* Java: 21
* Framework: Spring boot 3.2.x
* DBMS: MySQL

## Prerequisites
* Java SDK 21
* A MySQL server

## Start application
`mvn spring-boot:run`

## Build application
`mvn clean package`

## Docker guideline
## Build application
`docker build -t identity-service:0.9.0`
### Build docker image
`docker build -t hoag402/identity_service:0.9.0 .`
### Push docker image to Docker Hub
`docker image push hoag402/identity_service:0.9.0`
### Create network:
`docker network create hoagteria-network`
### Start MySQL in hoagteria-network
`docker run --network hoagteria-network --name mysql -p 3306:3306 -e MYSQL_ROOT_PASSWORD=601748 -d mysql:8.0.36-debian`
### Run your application in hoagteria-network
`docker run --name identity-service --network hoagteria-network -p 7777:7777 -e DBMS_CONNECTION=jdbc:mysql://mysql:3306/identity-service identity_service:0.9.0`

## Pull image in docker
`docker pull hoag402/identity_service`
## Create application in hoagteria-network
`docker run --name identity-service --network hoagteria-network -p 7777:7777 -e DBMS_CONNECTION=jdbc:mysql://mysql:3306/identity-service hoag402/identity_service:0.9.0`
