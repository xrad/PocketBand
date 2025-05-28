plugins {
    id("de.nullgrad.pocketband.kotlinMultiplatform")
}

kotlin {
    sourceSets {
        commonMain.dependencies {
            implementation(libs.kotlinx.coroutines.test)
            implementation(projects.devices.audio)
            implementation(projects.devices.stk)
            implementation(projects.data.audioassets)
            implementation(projects.data.midi)
            implementation(projects.data.presets)
            implementation(projects.domain.midi)
            implementation(projects.domain.plugins)
            implementation(projects.domain.synth)
            implementation(projects.domain.liveevents)
        }
    }
}
