# Message Management Server
The Message Management Server (MMS) is a server application that is responsible for managing messages. It is a part of the system and is used by the Message Management Client (MMC) to store and retrieve messages. It is a JAVA application that consists of an RabbitMQ message broker, and a SQL relational database.

## Setup
To setup and run the MMS, you need to have the following installed on your machine:
- Java 11
- Gradle
- MySQL Community Edition
- RabbitMQ

Alternatively, you can utilize the docker containers provided in the `docker-compose.yml` file. To run the MMS using docker, run the following command:
```bash
$ docker-compose up -d  # Run the MMS in the background
```