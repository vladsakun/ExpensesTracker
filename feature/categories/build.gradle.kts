plugins {
  alias(libs.plugins.expensestracker.android.feature)
  alias(libs.plugins.expensestracker.android.library.compose)
  //    alias(libs.plugins.vkompose)
}

// vkompose {
//    skippabilityCheck = true
//    // or
//    skippabilityCheck {
//        // For more see
//        // https://android-review.googlesource.com/c/platform/frameworks/support/+/2668595
//        // https://issuetracker.google.com/issues/309765121
//        stabilityConfigurationPath = "/path/file.config"
//    }
//
//    recompose {
//        isHighlighterEnabled = true
//        isLoggerEnabled = true
//        // or
//        logger {
//            logModifierChanges = true // true by default since 0.5.0
//            logFunctionChanges = true // true by default since 0.5.0. log when function arguments (like lambdas or function references) of composable function are changed
//        }
//    }
//
//    testTag {
//        isApplierEnabled = true
//        isDrawerEnabled = false
//        isCleanerEnabled = false
//    }
//
//    sourceInformationClean = true
// }

dependencies {
  api(projects.feature.categories.api)
  implementation(projects.sync.work)
  implementation(projects.feature.createTransaction.api)
  implementation(projects.feature.settings.api)

  implementation(libs.androidx.activity.compose)
}
