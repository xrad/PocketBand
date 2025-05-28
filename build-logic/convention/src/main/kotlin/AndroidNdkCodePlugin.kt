import com.android.build.gradle.LibraryExtension
import de.nullgrad.pocketband.convention.configureAndroidNdkCode
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.kotlin.dsl.configure

class AndroidNdkCodePlugin: Plugin<Project> {

    override fun apply(target: Project):Unit = with(target) {
        extensions.configure<LibraryExtension>(::configureAndroidNdkCode)
    }
}