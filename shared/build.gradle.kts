val datetime_version: String by project

plugins {
    kotlin("jvm")
    kotlin("plugin.serialization") version "1.8.0"
}

group = "com.kcchatapp"
version = "unspecified"

repositories {
    mavenCentral()
}

dependencies {
    implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:1.5.0-RC")
    implementation("org.jetbrains.kotlinx:kotlinx-datetime:$datetime_version")
}