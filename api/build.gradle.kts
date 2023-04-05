plugins {
    id ("war")
}

group = "com.epam.esm"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
}

dependencies {
    implementation(project(":core"))

    compileOnly("jakarta.servlet:jakarta.servlet-api:6.0.0")
    implementation("org.springframework:spring-webmvc:6.0.7")
    implementation("com.jayway.jsonpath:json-path:2.7.0")

    testImplementation("org.springframework:spring-test:6.0.7")
    testImplementation("org.junit.jupiter:junit-jupiter-api:5.8.1")
    testImplementation("org.mockito:mockito-junit-jupiter:5.2.0")

    testRuntimeOnly("org.junit.jupiter:junit-jupiter-engine:5.8.1")
}

tasks.getByName<Test>("test") {
    useJUnitPlatform()
}

tasks.war {
    version=version
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
        expand(mapOf("api_config" to "com.epam.esm.config"))
    }
}
