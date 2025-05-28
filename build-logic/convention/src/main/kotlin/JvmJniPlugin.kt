import de.nullgrad.pocketband.convention.configureJvmJni
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.kotlin.dsl.configure
import org.jetbrains.kotlin.gradle.dsl.KotlinMultiplatformExtension

class JvmJniPlugin: Plugin<Project> {

    override fun apply(target: Project):Unit = with(target) {
        extensions.configure<KotlinMultiplatformExtension>(::configureJvmJni)
    }
}