plugins {
  alias(libs.plugins.expensestracker.android.library)
  alias(libs.plugins.expensestracker.android.hilt)
}

android {
  defaultConfig {
    //        testInstrumentationRunner = "com.google.samples.apps.nowinandroid.core.testing.NiaTestRunner"
  }
  namespace = "com.emendo.expensestracker.sync"
}

dependencies {
  implementation(projects.appResources)
  implementation(projects.core.common)
  implementation(projects.core.datastore)
  implementation(projects.core.model)
  implementation(projects.core.data.api)

  implementation(libs.androidx.lifecycle.livedata.ktx)
  implementation(libs.androidx.tracing.ktx)
  implementation(libs.androidx.work.ktx)
  implementation(libs.hilt.ext.work)
  implementation(libs.kotlinx.coroutines.android)
  implementation(libs.kotlinx.serialization.json)

  kapt(libs.hilt.ext.compiler)

  androidTestImplementation(libs.androidx.work.testing)
}
