import com.emendo.expensestracker.ExpeBuildType
import org.jetbrains.kotlin.konan.properties.Properties

plugins {
  alias(libs.plugins.expensestracker.android.application)
  alias(libs.plugins.expensestracker.android.application.compose)
  alias(libs.plugins.expensestracker.android.hilt)
  alias(libs.plugins.module.assertion)
  alias(libs.plugins.baselineprofile)
  alias(libs.plugins.android.application)
}

android {
  compileSdk = libs.versions.compileSdk.get().toInt()
  namespace = "com.emendo.expensestracker"

  // Todo uncomment when baseline profile is ready
  experimentalProperties["android.experimental.r8.dex-startup-optimization"] = true

  defaultConfig {
    applicationId = "com.emendo.expensestracker"
    versionCode = 1
    versionName = "1.0"

    testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    vectorDrawables.useSupportLibrary = true
  }

  signingConfigs {
    create("release") {
      val keystoreProperties =
        Properties().apply {
          load(file(rootDir.path + "/keystore.properties").reader())
        }
      keyAlias = keystoreProperties.getProperty("KEY_ALIAS")
      keyPassword = keystoreProperties.getProperty("KEY_PASSWORD")
      storeFile = file(keystoreProperties.getProperty("STORE_FILE"))
      storePassword = keystoreProperties.getProperty("STORE_PASSWORD")
    }
  }

  buildTypes {
    getByName("release") {
      signingConfig = signingConfigs.getByName("release")
    }
    debug {
      applicationIdSuffix = ExpeBuildType.DEBUG.applicationIdSuffix
      isDebuggable = true
    }

    val release by getting {
      applicationIdSuffix = ExpeBuildType.RELEASE.applicationIdSuffix
      isMinifyEnabled = true
      isShrinkResources = true
      isDebuggable = false
      signingConfig = signingConfigs.getByName("release")
      proguardFiles(getDefaultProguardFile("proguard-android-optimize.txt"), "proguard-rules.pro")
    }
  }

  compileOptions {
    sourceCompatibility = JavaVersion.VERSION_18
    targetCompatibility = JavaVersion.VERSION_18
  }

  packaging {
    // Multiple dependency bring these files in. Exclude them to enable
    // our test APK to build (has no effect on our AARs)
    resources.excludes += "/META-INF/AL2.0"
    resources.excludes += "/META-INF/LGPL2.1"
  }

  //    vkompose {
  //        skippabilityCheck = true
  //        // or
  //        skippabilityCheck {
  //            // For more see
  //            // https://android-review.googlesource.com/c/platform/frameworks/support/+/2668595
  //            // https://issuetracker.google.com/issues/309765121
  //            stabilityConfigurationPath = "/path/file.config"
  //        }
  //
  //        recompose {
  //            isHighlighterEnabled = true
  //            isLoggerEnabled = true
  //            // or
  //            logger {
  //                logModifierChanges = true // true by default since 0.5.0
  //                logFunctionChanges = true // true by default since 0.5.0. log when function arguments (like lambdas or function references) of composable function are changed
  //            }
  //        }
  //
  //        testTag {
  //            isApplierEnabled = true
  //            isDrawerEnabled = false
  //            isCleanerEnabled = false
  //        }
  //
  //        sourceInformationClean = true
  //    }
}

dependencies {
  implementation(projects.feature.accounts)
  implementation(projects.feature.transactions)
  implementation(projects.feature.categories)
  implementation(projects.feature.settings)
  implementation(projects.feature.createTransaction)
  implementation(projects.feature.report)
  implementation(projects.feature.budget)

  implementation(projects.androidApi)
  implementation(projects.appBaseUi)
  implementation(projects.appBaseUi.api)
  implementation(projects.core.common)
  implementation(projects.core.data)
  implementation(projects.core.model)
  implementation(projects.core.designsystem)
  implementation(projects.core.ui)
  implementation(projects.core.domain)

  implementation(projects.sync.work)

  implementation(libs.androidx.activity.compose)
  implementation(libs.androidx.appcompat)
  implementation(libs.androidx.core.ktx)
  implementation(libs.androidx.core.splashscreen)
  implementation(libs.androidx.lifecycle.runtimeCompose)
  implementation(libs.androidx.compose.material3.windowSizeClass)
  implementation(libs.androidx.hilt.navigation.compose)
  implementation(libs.androidx.window.manager)
  implementation(libs.compose.destinations)
  implementation(libs.kotlinx.datetime)
  implementation(libs.rebugger)
  implementation(libs.androidx.profileinstaller)

  baselineProfile(projects.baselineprofile)

  ksp(libs.compose.destinations.ksp)
}
