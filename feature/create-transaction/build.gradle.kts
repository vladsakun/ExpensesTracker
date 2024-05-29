plugins {
    alias(libs.plugins.expensestracker.android.feature)
    alias(libs.plugins.expensestracker.android.library.compose)
}

dependencies {
    implementation(projects.feature.createTransaction.api)

    implementation(libs.kotlinx.datetime)
    implementation(projects.feature.accounts.api)
    implementation(projects.feature.categories.api)
}
