plugins {
    id("java")
}

group = "com.bruhdows"
version = "1.0.0"

repositories {
    mavenCentral()
}

tasks.withType<Jar> {
    manifest {
        attributes["Main-Class"] = "com.bruhdows.limbokeys.LimboKeys"
    }
}
