import com.google.devtools.ksp.gradle.KspExtension

plugins {
  id("expensestracker.android.library")
  id("expensestracker.android.library.compose")
  id("expensestracker.android.hilt")
  id("com.google.devtools.ksp")
}

android {
  namespace = "com.emendo.expensestracker.core.app.base.ui"

  configure<KspExtension> {
    arg("compose-destinations.mode", "navgraphs")
    arg("compose-destinations.moduleName", "app.base.ui")
    arg("compose-destinations.useComposableVisibility", "true")
  }
}

dependencies {
  implementation(project(":core:data"))
  implementation(project(":core:domain"))
  implementation(project(":core:common"))
  implementation(project(":core:ui"))
  implementation(project(":core:model"))
  implementation(project(":core:designsystem"))

  implementation(libs.androidx.activity.compose)
  implementation(libs.androidx.core.ktx)
  implementation(libs.coil.kt)
  implementation(libs.coil.kt.compose)
  implementation(libs.kotlinx.datetime)
  implementation(libs.kotlinx.immutable.collections)
  implementation(libs.compose.destinations)

  implementation(libs.androidx.hilt.navigation.compose)
  ksp(libs.compose.destinations.ksp)
}