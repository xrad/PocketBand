plugins {
    `kotlin-dsl`
}

group = "de.nullgrad.pocketband.buildlogic"

dependencies {
    compileOnly(libs.plugins.jetbrainsCompose.toDep())
    compileOnly(libs.plugins.androidApplication.toDep())
    compileOnly(libs.plugins.androidLibrary.toDep())
    compileOnly(libs.plugins.compose.compiler.toDep())
    compileOnly(libs.plugins.kotlinMultiplatform.toDep())
}

fun Provider<PluginDependency>.toDep() = map {
    "${it.pluginId}:${it.pluginId}.gradle.plugin:${it.version}"
}

gradlePlugin {
    plugins {
        register("kotlinMultiplatform"){
            id = "de.nullgrad.pocketband.kotlinMultiplatform"
            implementationClass = "KotlinMultiplatformPlugin"
        }
        register("composeMultiplatform"){
            id = "de.nullgrad.pocketband.composeMultiplatform"
            implementationClass = "ComposeMultiplatformPlugin"
        }
        register("androidNdkCode"){
            id = "de.nullgrad.pocketband.androidNdkCode"
            implementationClass = "AndroidNdkCodePlugin"
        }
        register("jvmJniCode"){
            id = "de.nullgrad.pocketband.jvmJniCode"
            implementationClass = "JvmJniPlugin"
        }
    }
}


