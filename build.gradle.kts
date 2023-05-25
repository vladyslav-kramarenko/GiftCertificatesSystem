plugins {}
repositories {
	mavenCentral()
}

val junitVersion = "5.9.2"

tasks.withType<JavaCompile> {
	options.encoding = "UTF-8"
}

dependencies {}