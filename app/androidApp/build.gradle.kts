@file:Suppress("UnstableApiUsage")

import com.android.build.gradle.internal.api.BaseVariantOutputImpl
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

val versionMajor: Int = project.findProperty("versionMajor")
    ?.toString()?.toIntOrNull()
    ?: throw IllegalArgumentException("Required Gradle property 'versionMajor' is not defined or is not an integer. Please define it in gradle.properties or as a command-line argument (-PversionMajor=<value>).")

val versionMinor: Int = project.findProperty("versionMinor")
    ?.toString()?.toIntOrNull()
    ?: throw IllegalArgumentException("Required Gradle property 'versionMinor' is not defined or is not an integer. Please define it in gradle.properties or as a command-line argument (-PversionMinor=<value>).")

val versionNum: Int = project.findProperty("versionNum")
    ?.toString()?.toIntOrNull()
    ?: throw IllegalArgumentException("Required Gradle property 'versionNum' is not defined or is not an integer. Please define it in gradle.properties or as a command-line argument (-PversionNum=<value>).")

fun versionCode(): Int {
    val code: Int = (versionMajor * 1000000) + (versionMinor * 1000) + versionNum
    println("versionCode is set to $code")
    return code
}

fun versionName(): String {
    val name = "${versionMajor}.${versionMinor}.${versionNum}"
    println("versionName is set to $name")
    return name
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
        getByName("debug") {
            applicationIdSuffix = ".debug"
            isDebuggable = true
        }
    }

    applicationVariants.all {
        if (this.buildType.name == "release") {
            val variant = this
            variant.outputs.all {
                (this as? BaseVariantOutputImpl)?.let {
                    it.outputFileName = "PocketBand-${variant.versionName}(${variant.versionCode})-release.apk"
                }
            }
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
