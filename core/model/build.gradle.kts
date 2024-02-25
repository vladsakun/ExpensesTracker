plugins {
  alias(libs.plugins.expensestracker.jvm.library)
  id("kotlinx-serialization")
}

dependencies {
  implementation(libs.kotlinx.datetime)
  implementation(libs.kotlinx.serialization.json)
}