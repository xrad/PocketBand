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
                arguments += "-DANDROID_STL=c++_shared"
                // I found no way to inject this to this place via build.gradle.kts.
                // I even tried to define a full custom extension interface, but the
                // code here runs at apply time and the build.gradle.kts config sections
                // have not been executed. We could always pass -DGENERATED_STK_HEADER_DIR
                // but then cmake will bark about unused variable. So I settled with
                // this hack.
                if (project.name == "stk") {
                    val generatedStkCpp = findProperty("generatedStkCpp") as String
                    val absolutePath = project.rootDir.resolve(generatedStkCpp)
                    arguments += "-DGENERATED_STK_HEADER_DIR=$absolutePath"
                }
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