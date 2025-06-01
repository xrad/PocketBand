import com.android.build.gradle.tasks.ExternalNativeBuildTask
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    id("de.nullgrad.pocketband.kotlinMultiplatform")
    id("de.nullgrad.pocketband.androidNdkCode")
    id("de.nullgrad.pocketband.jvmJniCode")
}

apply(from = "src/commonMain/stkgen/stkgen.gradle.kts")
tasks.withType<KotlinCompile>().configureEach {
    dependsOn("stk")
}
tasks.withType<ExternalNativeBuildTask>().configureEach {
    dependsOn("stk")
}

android {
    sourceSets["main"].assets.srcDirs("src/commonMain/resources")
}

jniBuild {
    jniLib = "build/jni/libstk.dylib"
    cmakeSourceDir = "src/androidMain/cpp/stk"
}

kotlin {
    jvm {
    }
}

kotlin {
    sourceSets {
        val commonMain by getting {
            kotlin.srcDir("build/generated/stkgen/kotlin/")
        }
    }
}
