plugins {
    alias(libs.plugins.expensestracker.android.feature)
    alias(libs.plugins.expensestracker.android.library.compose)
}

android {
    namespace = "com.emendo.expensestracker.features.user.settings"
}

//
// dependencies {
//  implementation(libs.androidx.activity.compose)
// }
