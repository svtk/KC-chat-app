pluginManagement {
    repositories {
        google()
        gradlePluginPortal()
        mavenCentral()
        maven("https://maven.pkg.jetbrains.space/public/p/compose/dev")
    }

    plugins {
        kotlin("jvm")
    }
}

rootProject.name = "KC-chat-app"
include(":desktop-client")
include(":server")
include("shared")
