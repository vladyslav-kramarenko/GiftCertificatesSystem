plugins {
    war
}
group = "com.epam.esm"
version = "0.0.1-SNAPSHOT"
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
    implementation("org.springframework.boot:spring-boot-configuration-processor:3.0.5")
    implementation("org.springframework.boot:spring-boot-starter-hateoas:3.0.5")
    implementation("org.springframework.boot:spring-boot-starter-web:3.0.5")
    implementation("org.springframework.boot:spring-boot-starter-data-jpa:3.0.5")
    implementation("org.springframework.boot:spring-boot-devtools:3.0.5")
    implementation("org.springframework.boot:spring-boot-starter-security:3.0.5")
    implementation("org.springframework.boot:spring-boot-starter-oauth2-resource-server:3.0.5")
    implementation("org.springframework.security.oauth.boot:spring-security-oauth2-autoconfigure:2.6.8")
    implementation("org.springframework.security:spring-security-oauth2-jose:6.0.2")
    implementation("com.auth0:auth0:2.2.0")

    implementation("org.glassfish:jakarta.el:4.0.2")
    implementation("org.hibernate.validator:hibernate-validator:8.0.0.Final")
    implementation("org.hibernate.validator:hibernate-validator-annotation-processor:8.0.0.Final")

    providedRuntime("org.springframework.boot:spring-boot-starter-tomcat:3.0.5")

    compileOnly("jakarta.servlet:jakarta.servlet-api:6.0.0")
    compileOnly("org.projectlombok:lombok:1.18.26")

    annotationProcessor("org.projectlombok:lombok:1.18.26")

    implementation("com.zaxxer:HikariCP:5.0.1")
    implementation("com.mysql:mysql-connector-j:8.0.32")

    testImplementation("org.springframework.boot:spring-boot-starter-test:3.0.5")
    testImplementation("com.h2database:h2:2.1.214")
    testImplementation("org.junit.jupiter:junit-jupiter-engine:5.9.2")
    testImplementation("org.junit.jupiter:junit-jupiter-api:5.9.2")
}

tasks.war {
    version = version
    archiveBaseName.set("gift-certificates-api")
    from(sourceSets["main"].output) {
        include("META-INF/**")
        include("**/*.class")
    }
    webInf {
        from("src/main/webapp/WEB-INF") {
            include("**/*")
        }
    }
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

