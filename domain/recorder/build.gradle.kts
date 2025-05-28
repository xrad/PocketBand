plugins {
    id("de.nullgrad.pocketband.kotlinMultiplatform")
}

kotlin {
    sourceSets {
        commonMain.dependencies {
            implementation(projects.devices.audio)
            implementation(projects.data.audioassets)
        }
    }
}
