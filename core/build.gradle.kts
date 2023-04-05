plugins {
    id("java-library")
}

version = "1.0.0"
group = "com.epam.esm"

repositories {
    mavenCentral()
}

dependencies {
    implementation("com.fasterxml.jackson.core:jackson-databind:2.14.2")
    implementation("com.fasterxml.jackson.datatype:jackson-datatype-jsr310:2.14.2")

    implementation("org.springframework:spring-jdbc:6.0.7")
    implementation("org.springframework:spring-tx:6.0.7")
    implementation("org.springframework:spring-context:6.0.7")

    implementation("org.springframework.data:spring-data-commons:3.0.4")

    implementation ("ch.qos.logback:logback-core:1.4.6")
    implementation ("ch.qos.logback:logback-classic:1.4.6")

    implementation("com.fasterxml.jackson.core:jackson-core:2.14.2")
    implementation("mysql:mysql-connector-java:8.0.32")
    implementation("com.jayway.jsonpath:json-path:2.7.0")
    implementation("com.zaxxer:HikariCP:5.0.1")

    testImplementation("org.springframework:spring-test:6.0.7")
    testImplementation("org.mockito:mockito-junit-jupiter:5.2.0")
    testImplementation("org.junit.jupiter:junit-jupiter-api:5.8.1")
    testImplementation("com.h2database:h2:2.1.214")
    testImplementation("org.hamcrest:hamcrest:2.2")
    testRuntimeOnly("org.junit.jupiter:junit-jupiter-engine:5.8.1")
}

tasks.getByName<Test>("test") {
    useJUnitPlatform()
}
tasks.jar {
    archiveBaseName.set("giftCertificatesSystem-core")
    manifest {
        attributes(mapOf("Implementation-Title" to project.name,
            "Implementation-Version" to project.version))
    }
}