import com.android.build.gradle.LibraryExtension
import com.emendo.expensestracker.Constants.NAMESPACE_PREFIX
import com.emendo.expensestracker.libs
import com.google.devtools.ksp.gradle.KspExtension
import org.gradle.api.Action
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.kotlin.dsl.configure
import org.gradle.kotlin.dsl.dependencies

class AndroidFeatureConventionPlugin : Plugin<Project> {
  override fun apply(target: Project) {
    with(target) {
      pluginManager.apply {
        apply("expensestracker.android.library")
        apply("expensestracker.android.hilt")
        apply("com.google.devtools.ksp")
      }
      val moduleName = path.split(":").last()

      extensions.configure<LibraryExtension> {
        namespace = NAMESPACE_PREFIX + moduleName
      }

      extensions.configure<KspExtension> {
        arg("compose-destinations.mode", "navgraphs")
        arg("compose-destinations.moduleName", moduleName)
        arg("compose-destinations.useComposableVisibility", "true")
      }

      dependencies {
        add("implementation", project(":core:designsystem"))
        add("implementation", project(":core:model"))
        add("implementation", project(":core:data"))
        add("implementation", project(":core:common"))

        add("implementation", libs.findLibrary("androidx.hilt.navigation.compose").get())
        add("implementation", libs.findLibrary("androidx.lifecycle.runtimeCompose").get())
        add("implementation", libs.findLibrary("androidx.lifecycle.viewModelCompose").get())
        add("implementation", libs.findLibrary("compose.destinations").get())
        add("ksp", libs.findLibrary("compose.destinations.ksp").get())

        add("implementation", libs.findLibrary("kotlinx.coroutines.android").get())
      }
    }
  }
}