plugins {
    java
    war
    jacoco
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
dependencies {
    implementation(project(":core"))
    implementation("org.springframework.boot:spring-boot-starter-data-jpa")
    implementation("org.springframework.boot:spring-boot-starter-hateoas")
    implementation("org.springframework.boot:spring-boot-starter-oauth2-client")
    implementation("org.springframework.boot:spring-boot-starter-security")
    implementation("org.springframework.boot:spring-boot-starter-web")
    developmentOnly("org.springframework.boot:spring-boot-devtools")

    providedRuntime("org.springframework.boot:spring-boot-starter-tomcat")

    testImplementation("org.springframework.boot:spring-boot-starter-test")
    testImplementation("org.springframework.security:spring-security-test")

    implementation("org.springframework.security.oauth.boot:spring-security-oauth2-autoconfigure:2.6.8")
    implementation("org.springframework.security:spring-security-oauth2-jose:6.0.2")
    implementation("org.springframework.security:spring-security-oauth2-resource-server:6.0.2")

    implementation("com.auth0:auth0:2.2.0")
    implementation("org.glassfish:jakarta.el:4.0.2")

    implementation("org.hibernate.validator:hibernate-validator:8.0.0.Final")
    implementation("org.hibernate.validator:hibernate-validator-annotation-processor:8.0.0.Final")
    compileOnly("jakarta.servlet:jakarta.servlet-api:6.0.0")

    compileOnly("org.projectlombok:lombok")
    annotationProcessor("org.projectlombok:lombok")

    implementation("com.zaxxer:HikariCP:5.0.1")
    implementation("com.mysql:mysql-connector-j:8.0.33")

    testImplementation("com.h2database:h2:2.1.214")
    testImplementation("org.junit.jupiter:junit-jupiter-engine:5.9.2")
    testImplementation("org.junit.jupiter:junit-jupiter-api:5.9.2")
}


tasks{
    test{
        useJUnitPlatform()
    }
    jacocoTestReport {
        reports {
            xml.isEnabled = true
            html.isEnabled = true
        }
    }
}

jacoco {
    toolVersion = "0.8.11"
}

