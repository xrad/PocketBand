plugins {
    id("de.nullgrad.pocketband.kotlinMultiplatform")
    id("de.nullgrad.pocketband.jvmJniCode")
}

jniBuild {
    jniLib = "build/jni/libmacos.dylib"
    cmakeSourceDir = "src/jvmMain/cpp"
}

kotlin {
    jvm {
        compilations["main"].defaultSourceSet {
            kotlin.srcDir("src/jvmMain/kotlin")
            kotlin.srcDir("src/jvmMain/cpp")
        }
    }
    sourceSets {
        commonMain.dependencies {
        }
    }
}
