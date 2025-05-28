plugins {
    id("de.nullgrad.pocketband.kotlinMultiplatform")
}

kotlin {
    sourceSets {
        commonMain.dependencies {
            compileOnly(libs.compose.stable.marker)
        }
    }
}
