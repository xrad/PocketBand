@file:Suppress("UnstableApiUsage")

import java.io.FileInputStream
import java.util.*

plugins {
    alias(libs.plugins.androidApplication)
    alias(libs.plugins.kotlinAndroid)
    alias(libs.plugins.compose.compiler)
}

val keystorePropertiesFile = rootProject.file("keystore.properties")
val keystoreProperties = Properties()
if (keystorePropertiesFile.exists()) {
    keystoreProperties.load(FileInputStream(keystorePropertiesFile))
}

val versionMajor = 1
val versionMinor = 0

val versionNum: String? by project

fun versionCode(): Int {
    versionNum?.let {
        val code: Int = (versionMajor * 1000000) + (versionMinor * 1000) + it.toInt()
        println("versionCode is set to $code")
        return code
    } ?: return 1
}

fun versionName(): String {
    versionNum?.let {
        val name = "${versionMajor}.${versionMinor}.${versionNum}"
        println("versionName is set to $name")
        return name
    } ?: return "1.0"
}

android {
    val ns = rootProject.ext.get("namespace")
    require(ns is String)

    namespace = ns

    compileSdk = libs.versions.android.compileSdk.get().toInt()

    // if we don't specify the NDK version to use, the default version defined
    // in the AGP will be active. If that is not installed, library symbols will
    // not be stripped ("Unable to strip the following libraries...")
    ndkVersion = libs.versions.android.compileNdk.get()

    val javaVersion = libs.versions.java.get()

    kotlinOptions {
        jvmTarget = javaVersion
    }

    defaultConfig {
        applicationId = ns
        minSdk = libs.versions.android.minSdk.get().toInt()
        targetSdk = libs.versions.android.targetSdk.get().toInt()
        versionCode = versionCode()
        versionName = versionName()
    }

    signingConfigs {
        create("pocketband") {
            keyAlias = "pocketband"
            keyPassword = "pocketband"
            storeFile = file("pocketband.keystore")
            storePassword = "pocketband"
        }
        create("release") {
            (keystoreProperties["keyPath"] as String?)?.let {
                storeFile = file(it)
            }
            keyAlias = keystoreProperties["keyAlias"] as String?
            keyPassword = keystoreProperties["keyPassword"] as String?
            storePassword = keystoreProperties["storePassword"] as String?
            enableV2Signing = true
        }
    }

    packaging {
        resources {
            excludes += "/META-INF/{AL2.0,LGPL2.1}"
        }
    }

    buildTypes {
        getByName("release") {
            isShrinkResources = true
            isMinifyEnabled = true
            signingConfig = signingConfigs.getByName("release")
            setProguardFiles(
                listOf(
                    getDefaultProguardFile("proguard-android.txt"),
                    "proguard-rules.pro"
                )
            )
        }
        create("githubRelease") {
            isShrinkResources = true
            isMinifyEnabled = true
            signingConfig = signingConfigs.getByName("pocketband")
            setProguardFiles(
                listOf(
                    getDefaultProguardFile("proguard-android.txt"),
                    "proguard-rules.pro"
                )
            )
        }
        getByName("debug") {
            applicationIdSuffix = ".debug"
            isDebuggable = true
            signingConfig = signingConfigs.getByName("pocketband")
        }
    }

    testOptions {
        unitTests {
            isIncludeAndroidResources = true
        }
    }

    compileOptions {
        sourceCompatibility = JavaVersion.toVersion(javaVersion)
        targetCompatibility = JavaVersion.toVersion(javaVersion)
    }

    buildFeatures {
        compose = true
    }

    dependencies {
        implementation(libs.androidx.activity.compose)
        implementation(projects.di)
        implementation(projects.app.ui)
    }
}
