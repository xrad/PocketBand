plugins {
    id("de.nullgrad.pocketband.kotlinMultiplatform")
    alias(libs.plugins.sqldelight)
}

kotlin {
    sourceSets {
        androidMain.dependencies {
            implementation(libs.sqldelight.android.driver)
        }
        jvmMain.dependencies {
            implementation(libs.sqldelight.jvm.driver)
        }
        commonMain.dependencies {
            implementation(libs.sqldelight.coroutines.extensions)
            compileOnly(libs.compose.stable.marker)
        }
    }
}

sqldelight {
    databases {
        create("PocketBandDatabase") {
            val ns = rootProject.ext.get("namespace")
            require(ns is String)
            packageName.set("${ns}.database")
        }
    }
}

