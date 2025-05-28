plugins {
    id("de.nullgrad.pocketband.kotlinMultiplatform")
}

kotlin {
    sourceSets {
        commonMain.dependencies {
            implementation(projects.data.midi)
            compileOnly(libs.compose.stable.marker)
        }
    }
}
