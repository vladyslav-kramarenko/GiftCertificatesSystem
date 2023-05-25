plugins {
    id("java-library")
    id("org.springframework.boot") version "3.1.0"
    id("io.spring.dependency-management") version "1.1.0"
}
group = "com.epam.esm"
version = "1"
java.sourceCompatibility = JavaVersion.VERSION_17
configurations {
    compileOnly {
        extendsFrom(configurations.annotationProcessor.get())
    }
}
repositories {
    mavenCentral()
}

apply(plugin = "io.spring.dependency-management")

dependencies {
    implementation("org.hibernate.validator:hibernate-validator:8.0.0.Final")
    implementation("jakarta.validation:jakarta.validation-api:3.0.2")
    implementation("org.glassfish:jakarta.el:4.0.2")

    implementation("org.springframework.boot:spring-boot-configuration-processor")
    implementation("org.springframework.boot:spring-boot-starter-data-jpa")
    implementation("org.springframework.boot:spring-boot-devtools")
    implementation("org.springframework.boot:spring-boot-starter-security")
    implementation("org.springframework.boot:spring-boot-starter-oauth2-resource-server")
    testImplementation("org.springframework.boot:spring-boot-starter-test")


    implementation("org.springframework.security.oauth.boot:spring-security-oauth2-autoconfigure:2.6.8")
    implementation("org.springframework.security:spring-security-config:6.0.2")
    implementation("org.springframework.security:spring-security-oauth2-jose:6.0.2")

    implementation("com.auth0:auth0:2.2.0")
    implementation("com.github.javafaker:javafaker:1.0.2")

    implementation("io.jsonwebtoken:jjwt-api:0.11.5")
    implementation("io.jsonwebtoken:jjwt-impl:0.11.5")
    runtimeOnly("io.jsonwebtoken:jjwt-jackson:0.11.5")

    implementation("com.fasterxml.jackson.core:jackson-databind:2.14.2")
    implementation("com.fasterxml.jackson.datatype:jackson-datatype-jsr310:2.14.2")

    compileOnly("jakarta.servlet:jakarta.servlet-api:6.0.0")
    compileOnly("org.projectlombok:lombok:1.18.26")

    annotationProcessor("org.projectlombok:lombok:1.18.26")

    implementation("com.zaxxer:HikariCP:5.0.1")
    implementation("com.mysql:mysql-connector-j:8.0.32")

    testRuntimeOnly("org.junit.jupiter:junit-jupiter-engine:5.9.2")

    testImplementation("com.h2database:h2:2.1.214")
    testImplementation("org.junit.jupiter:junit-jupiter-api:5.9.2")
    testImplementation("org.mockito:mockito-core:5.2.0")
}

tasks.jar {
    archiveBaseName.set("giftCertificatesSystem-core")
    manifest {
        attributes(
            mapOf(
                "Implementation-Title" to project.name,
                "Implementation-Version" to project.version
            )
        )
    }
}

tasks.named<org.springframework.boot.gradle.tasks.bundling.BootJar>("bootJar") {
    enabled = false
}
