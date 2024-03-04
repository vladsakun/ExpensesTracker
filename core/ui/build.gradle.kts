plugins {
  alias(libs.plugins.expensestracker.android.library)
  alias(libs.plugins.expensestracker.android.library.compose)
}

android {
  namespace = "com.emendo.expensestracker.core.ui"
}

dependencies {
  api(projects.core.modelUi)
  api(libs.rebugger)

  implementation(projects.core.designsystem)
  implementation(projects.core.model)

  api(libs.androidx.compose.foundation)
  api(libs.androidx.compose.foundation.layout)
  api(libs.androidx.compose.material.iconsExtended)
  api(libs.androidx.compose.material3)
  api(libs.androidx.compose.runtime)
  api(libs.androidx.compose.runtime.livedata)
  api(libs.androidx.compose.ui.tooling.preview)
  api(libs.androidx.compose.ui.util)
  api(libs.androidx.lifecycle.runtimeCompose)
  api(libs.androidx.metrics)
  api(libs.androidx.tracing.ktx)
  api(libs.compose.state.events)

  debugApi(libs.androidx.compose.ui.tooling)

  implementation(libs.androidx.activity.compose)
  implementation(libs.androidx.core.ktx)
  implementation(libs.coil.kt)
  implementation(libs.coil.kt.compose)
  implementation(libs.kotlinx.datetime)
  implementation(libs.kotlinx.immutable.collections)
  implementation(libs.compose.destinations)
}