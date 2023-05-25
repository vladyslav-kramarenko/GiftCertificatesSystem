plugins {
    id("org.springframework.boot") version "3.1.0"
    id("io.spring.dependency-management") version "1.1.0"
    java
    application
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
dependencies {
    implementation(project(":core"))
    implementation("org.springframework.boot:spring-boot-configuration-processor")
    implementation("org.springframework.boot:spring-boot-starter-hateoas")
    implementation("org.springframework.boot:spring-boot-starter-web")
    implementation("org.springframework.boot:spring-boot-starter-data-jpa")
    implementation("org.springframework.boot:spring-boot-devtools")
    implementation("org.springframework.boot:spring-boot-starter-security")
    implementation("org.springframework.boot:spring-boot-starter-oauth2-resource-server")

    testImplementation("org.springframework.boot:spring-boot-starter-test")
    implementation("org.springframework.security.oauth.boot:spring-security-oauth2-autoconfigure:2.6.8")
    implementation("org.springframework.security:spring-security-oauth2-jose:6.0.2")
    implementation("com.auth0:auth0:2.2.0")
    implementation("org.glassfish:jakarta.el:4.0.2")

    val hibernateValidatorVersion = "8.0.0.Final"
    implementation("org.hibernate.validator:hibernate-validator:${hibernateValidatorVersion}")
    implementation("org.hibernate.validator:hibernate-validator-annotation-processor:${hibernateValidatorVersion}")
    compileOnly("jakarta.servlet:jakarta.servlet-api:6.0.0")
    compileOnly("org.projectlombok:lombok:1.18.26")
    annotationProcessor("org.projectlombok:lombok:1.18.26")
    implementation("com.zaxxer:HikariCP:5.0.1")
    implementation("com.mysql:mysql-connector-j:8.0.32")

    testImplementation("com.h2database:h2:2.1.214")
    testImplementation("org.junit.jupiter:junit-jupiter-engine:5.9.2")
    testImplementation("org.junit.jupiter:junit-jupiter-api:5.9.2")
}
tasks.named<Copy>("processResources") {
    filesMatching("META-INF/context.xml") {
        expand(mapOf("api_config" to "com.epam.esm.api.config"))
    }
}
tasks.jar {
    manifest {
        attributes["Main-Class"] = "com.epam.esm.api.ApiApplication"
    }
}
application {
    mainClass.set("com.epam.esm.api.ApiApplication")
}
