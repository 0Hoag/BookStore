# Notification service

## Prerequisites

### Mongodb
Install Mongodb from Docker Hub

`docker pull bitnami/mongodb:7.0.11`

Start Mongodb server at port 27017 with root username and password: root/root

`docker run -d --name mongodb-7.0.11 -p 27019:27017 -e MONGODB_ROOT_USER=root -e MONGODB_ROOT_PASSWORD=601748 bitnami/mongodb:7.0.11`

create kafka
`docker-compose up -d`