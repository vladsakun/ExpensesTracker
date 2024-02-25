import com.emendo.expensestracker.ExpeBuildType
import org.jetbrains.kotlin.konan.properties.Properties

plugins {
  id("expensestracker.android.application")
  id("expensestracker.android.application.compose")
  id("expensestracker.android.hilt")
  alias(libs.plugins.module.assertion)
  alias(libs.plugins.baselineprofile)
  alias(libs.plugins.android.application)
}

android {
  compileSdk = libs.versions.compileSdk.get().toInt()

  // Todo uncomment when baseline profile is ready
//  experimentalProperties["android.experimental.r8.dex-startup-optimization"] = true

  defaultConfig {
    applicationId = "com.emendo.expensestracker"
    versionCode = 1
    versionName = "1.0"

    testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    vectorDrawables.useSupportLibrary = true
  }

  signingConfigs {
    create("release") {
      val keystoreProperties = Properties().apply {
        load(file(rootDir.path + "/keystore.properties").reader())
      }
      keyAlias = keystoreProperties.getProperty("KEY_ALIAS")
      keyPassword = keystoreProperties.getProperty("KEY_PASSWORD")
      storeFile = file(keystoreProperties.getProperty("STORE_FILE"))
      storePassword = keystoreProperties.getProperty("STORE_PASSWORD")
    }
  }

  buildTypes {
    debug {
      applicationIdSuffix = ExpeBuildType.DEBUG.applicationIdSuffix
      isDebuggable = true
    }

    val release by getting {
      applicationIdSuffix = ExpeBuildType.RELEASE.applicationIdSuffix
      isMinifyEnabled = true
      isShrinkResources = true
      isDebuggable = false
//      signingConfig = signingConfigs.getByName("release")
      proguardFiles(getDefaultProguardFile("proguard-android-optimize.txt"), "proguard-rules.pro")
    }

    create("benchmark") {
      // Enable all the optimizations from release build through initWith(release).
      initWith(release)
      matchingFallbacks += listOf("release")
      // Debug key signing is available to everyone.
      signingConfig = signingConfigs.getByName("debug")
      isDebuggable = false
      isMinifyEnabled = true
      proguardFiles("benchmark-rules.pro")
    }
  }

  compileOptions {
    sourceCompatibility = JavaVersion.VERSION_18
    targetCompatibility = JavaVersion.VERSION_18
  }

  namespace = "com.emendo.expensestracker"
}

dependencies {
  implementation(projects.feature.accounts)
  implementation(projects.feature.transactions)
  implementation(projects.feature.categories)
  implementation(projects.feature.settings)
  implementation(projects.feature.createTransaction)

  implementation(projects.core.androidApi)
  implementation(projects.core.appBaseUi)
  implementation(projects.core.common)
  implementation(projects.core.data)
  implementation(projects.core.model)
  implementation(projects.core.designsystem)
  implementation(projects.core.ui)

  implementation(projects.sync.work)

  implementation(libs.accompanist.systemuicontroller)
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
  //  implementation(libs.androidx.profileinstaller)

  baselineProfile(projects.baselineprofile)

  ksp(libs.compose.destinations.ksp)
}
