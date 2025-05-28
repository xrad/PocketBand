import org.gradle.internal.extensions.core.extra
import org.jetbrains.compose.desktop.application.dsl.TargetFormat

plugins {
    id("de.nullgrad.pocketband.kotlinMultiplatform")
    id("de.nullgrad.pocketband.composeMultiplatform")
}

kotlin {
    jvm {}
    sourceSets {
        jvmMain.dependencies {
            implementation(compose.desktop.currentOs)
            implementation(libs.kotlinx.coroutines.swing)
            implementation(projects.di)
            implementation(projects.app.ui)
        }
    }
}

compose.desktop {
    application {
        mainClass = "MainKt"
        nativeDistributions {
            targetFormats(TargetFormat.Dmg)
            modules("java.sql")
            // see https://github.com/Wavesonics/compose-multiplatform-file-picker/issues/87#issuecomment-1852979085
            modules("jdk.unsupported")

            packageName = "PocketBand"
            packageVersion = "1.0.0"
            copyright = "© 2024 Nullgrad Apps"
            macOS {
                packageVersion = "1.0.0"
                packageBuildVersion = "1.0.0"
                bundleID = "de.nullgrad.pocketband"
                dockName = "PocketBand"
                infoPlist {
                    extraKeysRawXml =
                        "  <key>NSMicrophoneUsageDescription</key>\n" +
                                "  <string>PocketBand needs access to the microphone to be able to record audio.</string>"
                }
                entitlementsFile.set(project.file("entitlements.plist"))
                runtimeEntitlementsFile.set(project.file("runtime-entitlements.plist"))
            }
        }
    }
}
