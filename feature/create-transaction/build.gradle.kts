plugins {
    alias(libs.plugins.expensestracker.android.feature)
    alias(libs.plugins.expensestracker.android.library.compose)
}

android {
  buildFeatures {
    // Disable R class generation for this module to reduce build time and size
    androidResources = false
  }
}

dependencies {
    api(projects.feature.createTransaction.api)

    implementation(libs.kotlinx.datetime)
    implementation(projects.feature.accounts.api)
    implementation(projects.feature.categories.api)
}
