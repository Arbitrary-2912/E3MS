# Use an official Amazon Corretto runtime as the base image
FROM amazoncorretto:17

# Install dependencies
RUN yum update -y && yum install -y wget unzip

# Install Gradle
ENV GRADLE_VERSION=7.6.1
RUN wget https://services.gradle.org/distributions/gradle-${GRADLE_VERSION}-bin.zip \
    && unzip gradle-${GRADLE_VERSION}-bin.zip -d /opt \
    && rm gradle-${GRADLE_VERSION}-bin.zip \
    && ln -s /opt/gradle-${GRADLE_VERSION} /opt/gradle
ENV PATH="/opt/gradle/bin:${PATH}"

# Set the working directory in the Docker image
WORKDIR /usr/src/app

# Copy the project files into the Docker image
COPY app /usr/src/app

# Ensure the Gradle wrapper is executable
RUN chmod +x ./gradlew

# Install the project dependencies and build the project
RUN ./gradlew build

# Expose the port that your app runs on
EXPOSE 8080

# Define the command to run your app
CMD ["java", "-jar", "build/libs/MessageManagementServer-1.0-MMS.jar"]
