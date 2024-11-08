Identity service
This microservice is responsible for:

Onboarding users
Roles and permissions
Authentication

Tech stack

Build tool: maven >= 3.9.5
Java: 21
Framework: Spring boot 3.2.x
DBMS: MongoDB

Prerequisites

## Java SDK 21
A MongoDB server

## Start application
`mvn spring-boot:run`
## Build application
`mvn clean package`



## Docker guideline
## Build application
`docker build -t book-service:0.0.1`
## Build docker image
`docker build -t hoag402/book_service:0.9.0 .`
## Push docker image to Docker Hub
`docker image push hoag402/book_service:0.9.0`
## Create network:
`docker network create book-network`
## Start MongoDB in book-network
`docker run --network book-network --name book-mongoDB -p 27017:27017 -d mongo:latest`
## Run your application in book-network
`docker run --name book-service --network book-network -p 8084:8084 -e SPRING_DATA_MONGODB_HOST=book-mongoDB -e SPRING_DATA_MONGODB_PORT=27017 -e SPRING_DATA_MONGODB_DATABASE=bookstore book-service:0.0.1`

## Pull image in docker
`docker pull hoag402/book-service`
## Create application in book-network
`docker run --name book-service --network book-network -p 8084:8084 -e SPRING_DATA_MONGODB_HOST=book-mongoDB -e SPRING_DATA_MONGODB_PORT=27017 -e SPRING_DATA_MONGODB_DATABASE=bookstore hoag402/book-service:0.0.1`