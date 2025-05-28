import com.android.build.gradle.LibraryExtension
import de.nullgrad.pocketband.convention.configureAndroidLibrary
import de.nullgrad.pocketband.convention.configureKotlinMultiplatform
import de.nullgrad.pocketband.convention.libs
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.kotlin.dsl.configure
import org.jetbrains.kotlin.gradle.dsl.KotlinMultiplatformExtension

// add kotlin multiplatform and android library plugins to project
open class KotlinMultiplatformPlugin: Plugin<Project> {

    override fun apply(target: Project):Unit = with(target) {
        with(pluginManager) {
            apply(libs.findPlugin("androidLibrary").get().get().pluginId)
            apply(libs.findPlugin("kotlinMultiplatform").get().get().pluginId)
        }
        extensions.configure<LibraryExtension>(::configureAndroidLibrary)
        extensions.configure<KotlinMultiplatformExtension>(::configureKotlinMultiplatform)
    }
}
