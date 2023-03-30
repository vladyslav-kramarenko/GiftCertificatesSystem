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
    implementation("org.springframework:spring-jdbc:6.0.7")
    implementation("org.springframework:spring-tx:6.0.7")
    implementation("org.springframework:spring-context:6.0.7")
    implementation("mysql:mysql-connector-java:8.0.32")
    implementation("com.zaxxer:HikariCP:5.0.1")

    testImplementation("org.springframework:spring-test:6.0.7")
    testImplementation("org.junit.jupiter:junit-jupiter-engine:5.9.2")
    testImplementation("org.mockito:mockito-junit-jupiter:5.2.0")
    testImplementation("com.h2database:h2:2.1.214")
    testImplementation("ch.qos.logback:logback-classic:1.4.6")
}

tasks.withType<Test> {
    useJUnitPlatform()
}
