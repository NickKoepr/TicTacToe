import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    application
    kotlin("jvm") version "1.6.0"
    id("com.github.johnrengelman.shadow") version "7.1.0"
}

group = "nl.nickkoepr.tictactoe"
version = "1.0.0"
val jdaVersion = "5.0.0-alpha.9"

repositories {
    mavenCentral()
    maven("https://m2.dv8tion.net/releases")
    maven("https://jitpack.io")
}

dependencies {
    testImplementation(kotlin("test"))
    implementation("net.dv8tion:JDA:$jdaVersion")
    implementation("mysql:mysql-connector-java:8.0.28")
    implementation("org.xerial:sqlite-jdbc:3.36.0.3")
    implementation("org.discordbots:DBL-Java-Library:2.0.1")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.6.0")
}

application {
    mainClass.set("nl.nickkoepr.tictactoe.TicTacToeKt")
}

tasks.test {
    useJUnit()
}

tasks.withType<KotlinCompile> {
    kotlinOptions.jvmTarget = "16"
}