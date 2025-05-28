@file:Suppress("UnstableApiUsage")

enableFeaturePreview("TYPESAFE_PROJECT_ACCESSORS")

pluginManagement {
    includeBuild("build-logic")
    repositories {
        google {
            mavenContent {
                includeGroupAndSubgroups("androidx")
                includeGroupAndSubgroups("com.android")
                includeGroupAndSubgroups("com.google")
            }
        }
        mavenCentral()
        gradlePluginPortal()
    }
}

dependencyResolutionManagement {
    repositories {
        google {
            mavenContent {
                includeGroupAndSubgroups("androidx")
                includeGroupAndSubgroups("com.android")
                includeGroupAndSubgroups("com.google")
            }
        }
        mavenCentral()
    }
}

rootProject.name = "PocketBand"

include(":di")
include(":data:presets")
include(":data:audioassets")
include(":data:midi")
include(":devices:sensors")
include(":devices:stk")
include(":devices:audio")
include(":devices:macos")
include(":domain:midi")
include(":domain:plugins")
include(":domain:liveevents")
include(":domain:edit")
include(":domain:recorder")
include(":domain:synth")
include(":domain:engine")
include(":test")
include(":app:design")
include(":app:ui")
include(":app:androidApp")
include(":app:desktopApp")
