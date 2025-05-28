@file:Suppress("UnstableApiUsage")

import org.jetbrains.compose.ExperimentalComposeLibrary
import org.jetbrains.kotlin.compose.compiler.gradle.ComposeFeatureFlag

plugins {
    id("de.nullgrad.pocketband.kotlinMultiplatform")
    id("de.nullgrad.pocketband.composeMultiplatform")
}

android {
    buildFeatures {
        compose = true
    }
}

compose {
    resources {
        packageOfResClass = "de.nullgrad.pocketband.design.generaded.resources"
    }
}

kotlin {
    sourceSets {
        androidMain.dependencies {
            implementation(libs.accompanist.permissions)
        }
        jvmMain.dependencies {
            implementation(projects.devices.macos)
        }
        commonMain.dependencies {
            implementation(projects.data.presets)
            implementation(projects.data.audioassets)
            implementation(projects.data.midi)
            implementation(projects.devices.sensors)
            implementation(projects.devices.stk)
            implementation(projects.devices.audio)
            implementation(projects.domain.liveevents)
            implementation(projects.domain.plugins)
            implementation(projects.domain.midi)
            implementation(projects.domain.recorder)
            implementation(projects.domain.synth)
            implementation(projects.domain.engine)
            implementation(projects.domain.edit)
            implementation(projects.app.design)
        }
        commonTest.dependencies {
            implementation(libs.kotlin.test)
            @OptIn(ExperimentalComposeLibrary::class)
            implementation(compose.uiTest)
        }
    }
}

composeCompiler {
    reportsDestination = layout.buildDirectory.dir("compose_compiler")
    metricsDestination = layout.buildDirectory.dir("compose_compiler")
    featureFlags = setOf(
        ComposeFeatureFlag.StrongSkipping
    )
}
