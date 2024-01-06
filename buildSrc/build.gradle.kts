plugins {
    `kotlin-dsl`
}

buildscript {
    dependencies {
        classpath(libs.kotlinPlugin)
    }
}

dependencies {
    implementation(libs.android.gradlePlugin)
    implementation(libs.kotlinPlugin)
    implementation(libs.ksp.gradlePlugin)
    implementation(libs.java.poet)
}
