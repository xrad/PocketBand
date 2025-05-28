plugins {
    id("de.nullgrad.pocketband.kotlinMultiplatform")
}

kotlin {
    sourceSets {
        commonMain.dependencies {
            implementation(projects.data.presets)
            implementation(projects.domain.plugins)
            implementation(projects.domain.synth)
            compileOnly(libs.compose.stable.marker)
        }
        commonTest.dependencies {
            implementation(libs.junit)
            implementation(libs.kotlinx.coroutines.test)
            implementation(projects.test)
        }
    }
}
