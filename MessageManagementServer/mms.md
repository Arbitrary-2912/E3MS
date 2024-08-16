# Message Management Server
The Message Management Server (MMS) is a server application that is responsible for managing messages. It is a part of the system and is used by the Message Management Client (MMC) to store and retrieve messages. It is a JAVA application that consists of an RabbitMQ message broker, and a SQL relational database.

## Setup
To set up and run the MMS, you need to have the following installed on your machine:
- Java 17
- Gradle
- MySQL Community Edition
- RabbitMQ

Alternatively, you can utilize the docker containers provided in the `docker-compose.yml` file. To run the MMS using docker, run the following command:
```bash
$ tar -xzvf e3ms.tar.gz  # Extract the docker image
$ docker load -i e3ms.tar  # Load the docker image
$ docker run -dp 127.0.0.1:8080:8080 e3ms  # Run the MMS in the background
```

To build the container run:
```bash
$ docker build -t e3ms .
```

To compress the container run:
```bash
$ docker save e3ms > e3ms.tar && tar -czvf e3ms.tar.gz e3ms.tar
```

To stop the MMS, run the following command:
```bash
$ docker stop $(docker ps -q --filter ancestor=e3ms)
```