package de.nullgrad.pocketband.convention

import org.gradle.api.Project
import org.gradle.kotlin.dsl.getByType
import org.jetbrains.compose.ComposePlugin
import org.jetbrains.kotlin.gradle.dsl.KotlinMultiplatformExtension

internal fun Project.configureComposeMultiplatform(
    extension: KotlinMultiplatformExtension
) = extension.apply {
    val composeDeps = extensions.getByType<ComposePlugin.Dependencies>()
    sourceSets.apply {
        commonMain {
            dependencies {
                implementation(composeDeps.runtime)
                implementation(composeDeps.foundation)
                implementation(composeDeps.material3)
                implementation(composeDeps.materialIconsExtended)
                implementation(composeDeps.material)
                implementation(composeDeps.ui)
                implementation(composeDeps.preview)
                implementation(composeDeps.components.resources)
                implementation(composeDeps.components.uiToolingPreview)
                implementation(libs.findLibrary("kotlinx-collections-immutable").get())
                implementation(libs.findLibrary("androidx-lifecycle-viewmodel-compose").get())
                implementation(libs.findLibrary("reorderable").get())
                implementation(libs.findLibrary("adaptive-navigation").get())
                implementation(libs.findLibrary("datastore").get())
                implementation(libs.findLibrary("datastore-preferences").get())
            }
        }
    }
}