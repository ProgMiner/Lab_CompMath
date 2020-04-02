import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    kotlin("jvm") version "1.3.71"
    id("com.github.johnrengelman.shadow") version "5.2.0"

    java
    idea
}

group = "ru.byprogminer"
version = "1.0-SNAPSHOT"

tasks.jar {
    manifest {
        attributes["Main-Class"] = "ru.byprogminer.compmath.lab3.MainKt"
    }
}

dependencies {
    implementation(kotlin("stdlib-jdk8"))
}

repositories {
    mavenCentral()
}

val compileKotlin: KotlinCompile by tasks
compileKotlin.kotlinOptions {
    jvmTarget = "1.8"
}

val compileTestKotlin: KotlinCompile by tasks
compileTestKotlin.kotlinOptions {
    jvmTarget = "1.8"
}

idea {
    module {
        isDownloadJavadoc = true
        isDownloadSources = true
    }
}
