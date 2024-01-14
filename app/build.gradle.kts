import java.io.FileInputStream
import java.util.Properties

plugins {
    id("com.android.application")
    kotlin("android")
    kotlin("kapt")
    id("kotlin-parcelize")
    id("com.google.gms.google-services")
    id("com.google.firebase.crashlytics")
    id("dagger.hilt.android.plugin")
}

val keystorePropertiesFile: File = rootProject.file("keystore.properties")
val keystoreProperties = Properties()
keystoreProperties.load(FileInputStream(keystorePropertiesFile))

android {
    signingConfigs {
        create("release") {
            keyAlias = keystoreProperties["keyAlias"] as String
            keyPassword = keystoreProperties["keyPassword"] as String
            storeFile = file(keystoreProperties["storeFile"]!!)
            storePassword = keystoreProperties["storePassword"] as String
        }
    }

    val versionMajor = 1
    val versionMinor = 12
    val versionPatch = 9
    val versionBuild = 1

    namespace = "com.moggot.findmycarlocation"
    compileSdk = 34
    defaultConfig {
        applicationId = "com.moggot.findmycarlocation"
        minSdk = 24
        targetSdk = 34
        versionCode =
            1000 * (1000 * versionMajor + 100 * versionMinor + versionPatch) + versionBuild
        versionName = "${versionMajor}.${versionMinor}.${versionPatch}"
        vectorDrawables.useSupportLibrary = true

        resValue("string", "GOOGLE_MAPS_ANDROID_API_KEY", keystoreProperties["google_map_api_key"] as String)
        javaCompileOptions { annotationProcessorOptions { arguments += mapOf("room.incremental" to "true") } }
    }
    buildTypes {
        release {
            isMinifyEnabled = true
            isShrinkResources = true
            signingConfig = signingConfigs.getByName("release")
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
        debug {
            versionNameSuffix = "-debug"
            isMinifyEnabled = false
        }
    }

    buildFeatures {
        viewBinding = true
        buildConfig = true
    }
    dataBinding {
        isEnabled = true
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
    kotlinOptions {
        jvmTarget = "17"
    }

}

dependencies {
    implementation(libs.core)
    implementation(libs.appCompat)
    implementation(libs.material)
    implementation(libs.constraintLayout)

    implementation(libs.googlePlayServicesMaps)
    implementation(libs.googlePlayServicesAnalytics)
    implementation(libs.googlePlayServicesLocation)
    implementation(libs.googlePlayServicesAds)

    implementation(libs.firebaseCore)
    implementation(libs.firebaseAds)
    implementation(libs.firebaseCrashlytics)
    implementation(libs.firebaseAnalytics)

    implementation(libs.lifecycle)
    implementation(libs.lifecycleExtension)
    implementation(libs.lifecycleViewModel)
    implementation(libs.lifecycleCommon)
    implementation(libs.fragment)

    implementation(libs.timber)

    implementation(libs.retrofit)
    implementation(libs.retrofitMoshi)
    implementation(libs.retrofitRxJava2)
    implementation(libs.okHttpInterceptor)

    implementation(libs.googleMaps)

    implementation(libs.hilt)
    kapt(libs.daggerHiltCompiler)

    implementation(libs.coLocation)
    implementation(libs.lifecycleRuntime)

    implementation(libs.billing)

    implementation(libs.roomRuntime)
    implementation(libs.room)
    kapt(libs.roomCompiler)
}
