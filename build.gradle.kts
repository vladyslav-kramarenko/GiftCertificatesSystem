plugins {
	java
	war
}

group = "com.epam.esm"
version = "0.0.1-SNAPSHOT"
java.sourceCompatibility = JavaVersion.VERSION_1_8

repositories {
	mavenCentral()
}

dependencies {
}

tasks.withType<Test> {
	useJUnitPlatform()
}
