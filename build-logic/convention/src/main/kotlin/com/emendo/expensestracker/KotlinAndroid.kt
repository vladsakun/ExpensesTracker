package com.emendo.expensestracker

import com.android.build.api.dsl.CommonExtension
import org.gradle.api.JavaVersion
import org.gradle.api.Project
import org.gradle.api.plugins.JavaPluginExtension
import org.gradle.kotlin.dsl.configure
import org.gradle.kotlin.dsl.provideDelegate
import org.jetbrains.kotlin.gradle.dsl.KotlinAndroidProjectExtension
import org.jetbrains.kotlin.gradle.dsl.KotlinJvmProjectExtension
import org.jetbrains.kotlin.gradle.dsl.KotlinTopLevelExtension

internal fun Project.configureKotlinAndroid(
  commonExtension: CommonExtension<*, *, *, *, *, *>,
) {
  commonExtension.apply {
    compileSdk = libs.findVersion("compileSdk").get().requiredVersion.toInt()

    defaultConfig {
      minSdk = libs.findVersion("minSdk").get().requiredVersion.toInt()
    }

    compileOptions {
      sourceCompatibility = JavaVersion.VERSION_18
      targetCompatibility = JavaVersion.VERSION_18
    }
  }

  configureKotlin<KotlinAndroidProjectExtension>()
}

/**
 * Configure base Kotlin options for JVM (non-Android)
 */
internal fun Project.configureKotlinJvm() {
  extensions.configure<JavaPluginExtension> {
    // Up to Java 11 APIs are available through desugaring
    // https://developer.android.com/studio/write/java11-minimal-support-table
    sourceCompatibility = JavaVersion.VERSION_18
    targetCompatibility = JavaVersion.VERSION_18
  }

  configureKotlin<KotlinJvmProjectExtension>()
}

private inline fun <reified T : KotlinTopLevelExtension> Project.configureKotlin() = configure<T> {
  // Treat all Kotlin warnings as errors (disabled by default)
  // Override by setting warningsAsErrors=true in your ~/.gradle/gradle.properties
  val warningsAsErrors: String? by project
  when (this) {
    is KotlinAndroidProjectExtension -> compilerOptions
    is KotlinJvmProjectExtension -> compilerOptions
    else -> TODO("Unsupported project extension $this ${T::class}")
  }.apply {
    //    jvmTarget = JvmTarget.JVM_11
    //    allWarningsAsErrors = warningsAsErrors.toBoolean()
    freeCompilerArgs.add(
      // Enable experimental coroutines APIs, including Flow
      "-opt-in=kotlinx.coroutines.ExperimentalCoroutinesApi",
    )
  }
}