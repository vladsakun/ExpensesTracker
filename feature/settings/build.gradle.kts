plugins {
    alias(libs.plugins.expensestracker.android.feature)
    alias(libs.plugins.expensestracker.android.library.compose)
}

dependencies {
  api(projects.feature.settings.api)
}
