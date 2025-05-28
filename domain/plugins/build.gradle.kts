plugins {
    id("de.nullgrad.pocketband.kotlinMultiplatform")
}

kotlin {
    sourceSets {
        commonMain.dependencies {
            implementation(libs.androidx.annotation)
            implementation(projects.devices.audio)
            implementation(projects.devices.stk)
            implementation(projects.devices.sensors)
            implementation(projects.data.audioassets)
            implementation(projects.data.presets)
            implementation(projects.data.midi)
            implementation(projects.domain.midi)
            implementation(projects.domain.liveevents)
            compileOnly(libs.compose.stable.marker)
        }
        commonTest.dependencies {
            implementation(libs.junit)
            implementation(libs.kotlinx.coroutines.test)
            implementation(projects.test)
            implementation(projects.domain.edit)
        }
    }
}
