plugins {
    java
    war
}

group = "com.epam.esm"
version = "0.0.1-SNAPSHOT"
java.sourceCompatibility = JavaVersion.VERSION_17

repositories {
    mavenCentral()
}

dependencies {
    compileOnly("jakarta.servlet:jakarta.servlet-api:6.0.0")

    implementation("com.fasterxml.jackson.core:jackson-databind:2.14.2")
    implementation("com.fasterxml.jackson.datatype:jackson-datatype-jsr310:2.14.2")

    implementation("org.springframework:spring-webmvc:6.0.7")
    implementation("org.springframework:spring-jdbc:6.0.7")
    implementation("org.springframework:spring-tx:6.0.7")
    implementation("org.springframework:spring-context:6.0.7")

    implementation("org.springframework.data:spring-data-commons:3.0.4")

    implementation ("ch.qos.logback:logback-core:1.4.6")
    implementation ("ch.qos.logback:logback-classic:1.4.6")

    implementation("com.fasterxml.jackson.core:jackson-core:2.14.2")
    implementation("mysql:mysql-connector-java:8.0.32")
    implementation("com.zaxxer:HikariCP:5.0.1")
    implementation("com.jayway.jsonpath:json-path:2.7.0")

    testImplementation("org.springframework:spring-test:6.0.7")
    testImplementation("org.junit.jupiter:junit-jupiter-engine:5.9.2")
    testImplementation("org.mockito:mockito-junit-jupiter:5.2.0")
    testImplementation("com.h2database:h2:2.1.214")
    testImplementation("org.hamcrest:hamcrest:2.2")
    testImplementation("jakarta.servlet:jakarta.servlet-api:6.0.0")
}

tasks.withType<Test> {
    useJUnitPlatform()
}
