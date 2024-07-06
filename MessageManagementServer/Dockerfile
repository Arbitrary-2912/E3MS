# Use an official openjdk runtime as the base image
FROM openjdk:17

# Set the working directory in the Docker image
WORKDIR /usr/src/app

# Copy the project files into the Docker image
COPY . .

# Install the project dependencies
RUN ./gradlew build

# Expose the port that your app runs on
EXPOSE 8080

# Define the command to run your app
CMD [ "java", "-jar", "build/libs/your-app-name.jar" ]