plugins {
    id("java")
}

group = "com.bruhdows"
version = "0.1.0"

repositories {
    mavenCentral()
}

tasks.withType<Jar> {
    manifest {
        attributes["Main-Class"] = "com.bruhdows.limbokeys.LimboKeys"
    }
}

tasks.register<Copy>("copyJarToDocs") {
    from("build/libs/LimboKeys-0.1.0.jar")
    into("docs")
}

tasks.build {
    finalizedBy("copyJarToDocs")
}
