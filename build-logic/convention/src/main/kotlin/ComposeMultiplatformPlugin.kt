import de.nullgrad.pocketband.convention.configureComposeMultiplatform
import de.nullgrad.pocketband.convention.libs
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.kotlin.dsl.configure
import org.jetbrains.kotlin.gradle.dsl.KotlinMultiplatformExtension

// add compose multiplatform plugins to project
// requires kotlin multiplatform plugin has been applied before
// see also KotlinMultiplatformPlugin
class ComposeMultiplatformPlugin: Plugin<Project> {

    override fun apply(target: Project):Unit = with(target) {
        with(pluginManager) {
            apply(libs.findPlugin("jetbrainsCompose").get().get().pluginId)
            apply(libs.findPlugin("compose-compiler").get().get().pluginId)
        }
        extensions.configure<KotlinMultiplatformExtension>(::configureComposeMultiplatform)
    }
}