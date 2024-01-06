// Top-level build file where you can add configuration options common to all sub-projects/modules.

buildscript {
    repositories {
        google()
        mavenCentral()
        gradlePluginPortal()
        maven("https://plugins.gradle.org/m2/")
    }
    dependencies {
        classpath(libs.googlePlayServicesPlugin)
        classpath(libs.hiltPlugin)
        classpath(libs.kotlinPlugin)
        classpath(libs.android.gradlePlugin)
        classpath(libs.firebaseCrashlyticsPlgin)
    }
}

allprojects {
    repositories {
        google()
        mavenCentral()
        maven(url = "https://jitpack.io")
    }
}

task("clean", Delete::class) {
    delete = setOf(rootProject.buildDir)
}
