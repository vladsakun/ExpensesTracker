import com.android.build.gradle.LibraryExtension
import com.emendo.expensestracker.Constants.NAMESPACE_PREFIX
import com.emendo.expensestracker.composeDestinations
import com.emendo.expensestracker.libs
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

      composeDestinations(moduleName)

      dependencies {
        add("implementation", project(":core:model"))
        add("implementation", project(":core:ui"))
        add("implementation", project(":core:designsystem"))
        add("implementation", project(":core:data:api"))
        add("implementation", project(":core:common"))
        add("implementation", project(":core:domain"))
        add("implementation", project(":app-base-ui:api"))

        add("implementation", libs.findLibrary("androidx.hilt.navigation.compose").get())
        add("implementation", libs.findLibrary("androidx.lifecycle.viewModelCompose").get())
        add("implementation", libs.findLibrary("compose.destinations").get())
        add("implementation", libs.findLibrary("kotlinx.coroutines.android").get())
        add("implementation", libs.findLibrary("kotlinx.immutable.collections").get())

        add("ksp", libs.findLibrary("compose.destinations.ksp").get())
      }
    }
  }
}