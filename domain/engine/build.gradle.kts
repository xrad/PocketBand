plugins {
    id("de.nullgrad.pocketband.kotlinMultiplatform")
}

kotlin {
    sourceSets {
        commonMain.dependencies {
            implementation(projects.domain.synth)
            implementation(projects.domain.recorder)
        }
    }
}
