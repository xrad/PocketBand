package de.nullgrad.pocketband.convention

import org.gradle.api.Project
import org.jetbrains.kotlin.gradle.dsl.KotlinMultiplatformExtension

fun KotlinMultiplatformExtension.dependencyInjection(project: Project) {

    if (project.name == "di") {
        return
    }

    sourceSets.apply {
        commonMain {
            dependencies {
                project.rootProject.subprojects.find {  it.name == "di" }?.let {
                    val diDependency = it.rootProject.dependencyFactory.create(it)
                    implementation(diDependency)
                }
            }
        }
    }

}