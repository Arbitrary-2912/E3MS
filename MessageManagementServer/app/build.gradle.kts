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
    implementation("org.slf4j:slf4j-api:1.7.32")
    implementation("ch.qos.logback:logback-classic:1.2.3")
    implementation("com.google.code.gson:gson:2.11.0")
    implementation("org.apache.logging.log4j:log4j-api:2.14.1")
}
java {
    sourceCompatibility = JavaVersion.VERSION_17
    targetCompatibility = JavaVersion.VERSION_17
}

tasks.test {
    useJUnitPlatform()
}

tasks.jar {
    manifest {
        attributes["Main-Class"] = "system.REST"
    }
    from(configurations.runtimeClasspath.get().filter { it.exists() }.map { if (it.isDirectory) it else zipTree(it) })
    duplicatesStrategy = DuplicatesStrategy.EXCLUDE
}