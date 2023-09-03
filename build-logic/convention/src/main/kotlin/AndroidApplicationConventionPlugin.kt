import com.android.build.api.dsl.ApplicationExtension
import com.android.build.api.variant.ApplicationAndroidComponentsExtension
import com.android.build.gradle.internal.cxx.configure.gradleLocalProperties
import com.emendo.expensestracker.configureKotlinAndroid
import com.emendo.expensestracker.libs
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.kotlin.dsl.configure

class AndroidApplicationConventionPlugin : Plugin<Project> {
  override fun apply(target: Project) {
    with(target) {
      with(pluginManager) {
        apply("com.android.application")
        apply("org.jetbrains.kotlin.android")
        apply("com.google.devtools.ksp")
      }

      extensions.configure<ApplicationExtension> {
        configureKotlinAndroid(this)
        defaultConfig {
          targetSdk = libs.findVersion("targetSdk").get().requiredVersion.toInt()
          resourceConfigurations += setOf("en")
          ndk {
            val localProperties = gradleLocalProperties(rootDir)
            val localAbiFilters = localProperties.getProperty("android.abis").split(",")
            // Removing all ABI except ARM
            // Remove if we need to support x86
            abiFilters += localAbiFilters
          }
        }
      }
      extensions.configure<ApplicationAndroidComponentsExtension> {}
    }
  }
}