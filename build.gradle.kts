plugins {
	java
	id("org.springframework.boot") version "3.0.5"
	id("io.spring.dependency-management") version "1.1.0"
	kotlin("jvm") version "1.8.20"
}

group = "com.epam.esm"
version = "1.0-SNAPSHOT"

repositories {
	mavenCentral()
}

val junitVersion = "5.9.2"

tasks.withType<JavaCompile> {
	options.encoding = "UTF-8"
}

dependencies {
	// Add your dependencies here
}

tasks.test {
	useJUnitPlatform()
}