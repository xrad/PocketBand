package de.nullgrad.pocketband.convention

import com.android.build.api.dsl.LibraryExtension
import org.gradle.api.JavaVersion
import org.gradle.api.Project
import org.gradle.internal.extensions.core.extra

internal fun Project.configureAndroidLibrary(
    extension: LibraryExtension
) = extension.apply {

    val moduleName = path.split(":").filter { it.isNotEmpty() }.joinToString(".")
    require(moduleName.isNotEmpty())
    val ns = rootProject.extra.get("namespace")
    namespace = "$ns.$moduleName"

    compileSdk = libs.findVersion("android-compileSdk").get().requiredVersion.toInt()
    defaultConfig {
        minSdk = libs.findVersion("android-minSdk").get().requiredVersion.toInt()
    }
    compileOptions {
        val versionString = libs.findVersion("java").get()
        val javaVersion = JavaVersion.toVersion(versionString)
        sourceCompatibility = javaVersion
        targetCompatibility = javaVersion
    }
    packaging {
        resources {
            excludes += "/META-INF/{AL2.0,LGPL2.1}"
        }
    }
}
