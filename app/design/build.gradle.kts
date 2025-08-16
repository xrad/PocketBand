@file:Suppress("UnstableApiUsage")

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
        packageOfResClass = "de.nullgrad.pocketband.design.generated.resources"
    }
}

composeCompiler {
    reportsDestination = layout.buildDirectory.dir("compose_compiler")
    metricsDestination = layout.buildDirectory.dir("compose_compiler")
}

