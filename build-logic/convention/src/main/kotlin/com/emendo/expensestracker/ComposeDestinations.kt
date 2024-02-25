package com.emendo.expensestracker

import com.google.devtools.ksp.gradle.KspExtension
import org.gradle.api.Project
import org.gradle.kotlin.dsl.configure

fun Project.composeDestinations(moduleName: String) {
  with(project) {
    extensions.configure<KspExtension> {
      arg("compose-destinations.mode", "navgraphs")
      arg("compose-destinations.moduleName", moduleName)
      arg("compose-destinations.useComposableVisibility", "true")
    }
  }
}