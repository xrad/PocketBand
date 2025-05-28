package de.nullgrad.pocketband.convention

import org.gradle.api.Project
import org.gradle.kotlin.dsl.jvm
import org.jetbrains.kotlin.gradle.dsl.JvmTarget
import org.jetbrains.kotlin.gradle.dsl.KotlinMultiplatformExtension

internal fun Project.configureKotlinMultiplatform(
    extension: KotlinMultiplatformExtension
) = extension.apply {

    val jvmVersionString = libs.findVersion("java").get().toString()
    val jvmVersion = JvmTarget.fromTarget(jvmVersionString)

    androidTarget {
        compilerOptions {
            jvmTarget.set(jvmVersion)
        }
    }
    jvm {}

    applyDefaultHierarchyTemplate()
    dependencyInjection(project)
    
    sourceSets.apply {
        commonMain {
            dependencies {
                implementation(libs.findLibrary("kotlinx-coroutines-core").get())
            }
        }
    }
}