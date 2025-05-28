plugins {
    id("de.nullgrad.pocketband.kotlinMultiplatform")
}

android {
    sourceSets["main"].assets.srcDirs("src/commonMain/resources")
}

kotlin {
    sourceSets {
        commonMain.dependencies {
            compileOnly(libs.compose.stable.marker)
        }
    }
}
