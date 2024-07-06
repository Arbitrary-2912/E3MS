plugins {
    id("java")
}

group = "com.example"
version = "1.0-MMS"

repositories {
    mavenCentral()
}

dependencies {
    implementation("javax.inject:javax.inject:1")
    implementation("mysql:mysql-connector-java:8.0.28")
    implementation("com.google.inject:guice:5.0.1")
    implementation("com.rabbitmq:amqp-client:5.17.1")
    implementation("com.google.code.gson:gson:2.11.0")
    implementation("org.apache.logging.log4j:log4j-api:2.14.1")
}
java {
    sourceCompatibility = JavaVersion.VERSION_14
    targetCompatibility = JavaVersion.VERSION_14
}

tasks.test {
    useJUnitPlatform()
}