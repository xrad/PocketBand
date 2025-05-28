package de.nullgrad.pocketband.convention

import com.android.build.api.dsl.LibraryExtension
import org.gradle.api.Project

@Suppress("UnstableApiUsage")
internal fun Project.configureAndroidNdkCode(
    extension: LibraryExtension
) = extension.apply {

    ndkVersion = libs.findVersion("android-compileNdk").get().requiredVersion

    defaultConfig {
        externalNativeBuild {
            cmake {
                arguments("-DANDROID_STL=c++_shared")
            }
        }
    }
    externalNativeBuild {
        cmake {
            path = file("src/androidMain/cpp/CMakeLists.txt")
            version = libs.findVersion("android-cmake").get().requiredVersion
        }
    }
}