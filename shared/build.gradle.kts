plugins {
    kotlin("jvm")
    kotlin("plugin.serialization") version "1.7.22"
}

group = "com.kcchatapp"
version = "unspecified"

repositories {
    mavenCentral()
}

dependencies {
    implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:1.5.0-RC")
    implementation("org.jetbrains.kotlinx:kotlinx-datetime:0.4.0")
}