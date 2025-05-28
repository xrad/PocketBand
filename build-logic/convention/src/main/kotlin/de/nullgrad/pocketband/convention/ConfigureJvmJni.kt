package de.nullgrad.pocketband.convention

import com.android.build.api.variant.AndroidComponentsExtension
import org.gradle.api.GradleException
import org.gradle.api.Project
import org.gradle.api.tasks.Exec
import org.gradle.api.tasks.bundling.Jar
import org.gradle.kotlin.dsl.named
import org.gradle.kotlin.dsl.register
import org.jetbrains.kotlin.gradle.dsl.KotlinMultiplatformExtension
import java.nio.file.Paths

private fun Project.getJavaHome() : String {
    return project.properties["org.gradle.java.home"] as String
}

private fun getNumCpus() : Int {
    return Runtime.getRuntime().availableProcessors()
}

private fun Project.getAndroidCmake() : String {
    val androidExtension = project.extensions.getByType(AndroidComponentsExtension::class.java)
    val sdkPath = androidExtension.sdkComponents.sdkDirectory.get().toString()
    val cmakeVersion = libs.findVersion("android-cmake").get().requiredVersion
    val cmakeFile = Paths.get(sdkPath, "cmake", cmakeVersion, "bin", "cmake").toFile()
    if (!cmakeFile.exists()) {
        throw GradleException("CMake not found at $cmakeFile")
    }
    return cmakeFile.absolutePath
}

open class JniBuildExtension {
    var jniLib: String = ""
    var cmakeSourceDir: String = ""
}

internal fun Project.configureJvmJni(
    extension: KotlinMultiplatformExtension
) = extension.apply {
    val javahome = getJavaHome()

    val jniBuild = project.extensions.create("jniBuild", JniBuildExtension::class.java)

    val cmake = getAndroidCmake()

    val cmakeBuildDir = file("build/jni")

    tasks.register<Exec>("configureCMake") {
        group = "build"
        description = "Configure the CMake build"

        require(jniBuild.cmakeSourceDir.isNotEmpty())
        val sourceDir = file(jniBuild.cmakeSourceDir)

        inputs.dir(sourceDir)
        outputs.dir(cmakeBuildDir)

        doFirst {
            cmakeBuildDir.mkdirs()

            require(cmake.isNotEmpty())
            val jniInclude = "$javahome/include"
            val jniIncludeMd = "$javahome/include/darwin"
            val cmakeCxxFlags = "-I\"$jniInclude\" -I\"$jniIncludeMd\""

            workingDir = cmakeBuildDir
            commandLine(
                cmake,
                sourceDir.absolutePath,
                "-DCMAKE_CXX_FLAGS=$cmakeCxxFlags"
            )
        }
    }

    tasks.register<Exec>("buildCMake") {
        dependsOn("configureCMake")

        group = "build"
        description = "Build CMake build"

        workingDir = cmakeBuildDir
        inputs.dir(cmakeBuildDir)
        require(jniBuild.jniLib.isNotEmpty())
        outputs.file(jniBuild.jniLib)

        doFirst {
            val numJobs = getNumCpus() - 1
            commandLine("make", "-j$numJobs")
        }
    }

    tasks.named("compileKotlinJvm") {
        dependsOn("buildCMake")
    }

    tasks.named<Jar>("jvmJar") {
        require(jniBuild.jniLib.isNotEmpty())
        inputs.file(jniBuild.jniLib)
        from(jniBuild.jniLib) {
            into("/")
        }
    }
}