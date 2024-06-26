import com.android.build.api.dsl.ApplicationExtension
import com.emendo.expensestracker.applyComposePlugins
import com.emendo.expensestracker.configureAndroidCompose
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.kotlin.dsl.apply
import org.gradle.kotlin.dsl.getByType

class AndroidApplicationComposeConventionPlugin : Plugin<Project> {
    override fun apply(target: Project) {
        with(target) {
            apply(plugin = "com.android.application")
            //            apply(plugin = "com.vk.vkompose")
            applyComposePlugins()
            val extension = extensions.getByType<ApplicationExtension>()
            configureAndroidCompose(extension)
        }
    }
}
