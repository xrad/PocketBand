plugins {
    id("de.nullgrad.pocketband.kotlinMultiplatform")
}

kotlin {
    sourceSets {
        commonMain.dependencies {
            implementation(projects.devices.audio)
            implementation(projects.devices.stk)
            implementation(projects.data.presets)
            implementation(projects.data.audioassets)
            implementation(projects.data.midi)
            implementation(projects.domain.plugins)
            implementation(projects.domain.liveevents)
            implementation(projects.domain.midi)
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
