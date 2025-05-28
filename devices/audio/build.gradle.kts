plugins {
    id("de.nullgrad.pocketband.kotlinMultiplatform")
    id("de.nullgrad.pocketband.androidNdkCode")
    id("de.nullgrad.pocketband.jvmJniCode")
    idea
}

android {
    buildFeatures {
        prefab = true
    }
    dependencies {
        implementation(libs.oboe)
    }
}

jniBuild {
    jniLib = "build/jni/pocketband/libpocketband.dylib"
    cmakeSourceDir = "src/androidMain/cpp"
}

idea {
    module {
        // Add macOS system header paths to the IDE for visibility
        sourceDirs.add(file("src/main/cpp"))  // Ensure your JNI source directory is included
        sourceDirs.add(file("/Library/Developer/CommandLineTools/SDKs/MacOSX11.3.sdk/System/Library/Frameworks"))
    }
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
            compileOnly(libs.compose.stable.marker)
        }
    }
}
