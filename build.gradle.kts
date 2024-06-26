plugins {
  alias(libs.plugins.android.application) apply false
  alias(libs.plugins.kotlin.jvm) apply false
  alias(libs.plugins.kotlin.serialization) apply false
  alias(libs.plugins.hilt) apply false
  alias(libs.plugins.ksp) apply false
  alias(libs.plugins.compose.compiler.report) apply false
  alias(libs.plugins.android.test) apply false
  alias(libs.plugins.baselineprofile) apply false
  alias(libs.plugins.kotlinAndroid) apply false
  alias(libs.plugins.module.assertion) apply false
  //    alias(libs.plugins.vkompose) apply false
  alias(libs.plugins.kotlin.compose.compiler) apply false
}

buildscript {
  repositories {
    google()
    mavenCentral()
  }
}

subprojects {
  tasks.withType<org.jetbrains.kotlin.gradle.tasks.KotlinCompile>().configureEach {
    compilerOptions {
      if (project.findProperty("composeCompilerReports") == "true") {
        freeCompilerArgs.addAll(
          listOf(
            "-P",
            "plugin:androidx.compose.compiler.plugins.kotlin:reportsDestination=${project.buildDir.absolutePath}/compose_compiler",
          )
        )
      }
      if (project.findProperty("composeCompilerMetrics") == "true") {
        freeCompilerArgs.addAll(
          listOf(
            "-P",
            "plugin:androidx.compose.compiler.plugins.kotlin:metricsDestination=${project.buildDir.absolutePath}/compose_compiler",
          )
        )
      }
      freeCompilerArgs.addAll(
        listOf(
          "-P",
          "plugin:androidx.compose.compiler.plugins.kotlin:experimentalStrongSkipping=true",
        )
      )

    }
    //    kotlinOptions {
    //      if (project.findProperty("composeCompilerReports") == "true") {
    //        freeCompilerArgs +=
    //          listOf(
    //            "-P",
    //            "plugin:androidx.compose.compiler.plugins.kotlin:reportsDestination=${project.buildDir.absolutePath}/compose_compiler",
    //          )
    //      }
    //      if (project.findProperty("composeCompilerMetrics") == "true") {
    //        freeCompilerArgs +=
    //          listOf(
    //            "-P",
    //            "plugin:androidx.compose.compiler.plugins.kotlin:metricsDestination=${project.buildDir.absolutePath}/compose_compiler",
    //          )
    //      }
    //    }
    //    compilerOptions.freeCompilerArgs.addAll(
    //      "-P",
    //      "plugin:androidx.compose.compiler.plugins.kotlin:experimentalStrongSkipping=true",
    //    )
  }
}
