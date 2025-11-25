import com.android.build.gradle.LibraryExtension
import com.emendo.expensestracker.configureKotlinAndroid
import com.emendo.expensestracker.libs
import com.emendo.expensestracker.setupExpeFeatures
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.kotlin.dsl.configure
import org.gradle.kotlin.dsl.dependencies

class AndroidLibraryConventionPlugin : Plugin<Project> {
  override fun apply(target: Project) {
    with(target) {
      with(pluginManager) {
        apply("com.android.library")
        apply("org.jetbrains.kotlin.android")
        apply("kotlin-parcelize")
      }

      extensions.configure<LibraryExtension> {
        configureKotlinAndroid(this)
        defaultConfig {
          resourceConfigurations += setOf("en")
        }
      }

      dependencies {
        add("testImplementation", libs.findLibrary("junit").get())
        add("testImplementation", libs.findLibrary("mockk").get())
        add("testImplementation", libs.findLibrary("turbine").get())
        add("testImplementation", libs.findLibrary("kotlinx-coroutines-test").get())
      }

      setupExpeFeatures()
    }
  }
}