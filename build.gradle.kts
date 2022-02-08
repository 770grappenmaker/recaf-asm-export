plugins {
    kotlin("jvm") version "1.6.10"
    id("org.openjfx.javafxplugin") version "0.0.10"
}

group = "com.grappenmaker"
version = "1.0"

repositories {
    mavenCentral()
    maven("https://jitpack.io")
    maven("https://files.minecraftforge.net/maven")
}

dependencies {
    compileOnly("com.github.Col-E:Recaf:2.18.3")
}

tasks.jar {
    from(configurations.runtimeClasspath.get().map { if (it.isDirectory) it else zipTree(it) })
    duplicatesStrategy = DuplicatesStrategy.EXCLUDE
}

javafx {
    version = "16"
    modules = listOf("javafx.graphics", "javafx.controls")
    configuration = "compileOnly"
}