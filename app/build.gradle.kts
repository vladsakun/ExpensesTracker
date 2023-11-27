import com.emendo.expensestracker.ExpeBuildType

plugins {
  id("expensestracker.android.application")
  id("expensestracker.android.application.compose")
  id("expensestracker.android.hilt")
}

android {
  compileSdk = libs.versions.compileSdk.get().toInt()

  defaultConfig {
    applicationId = "com.emendo.expensestracker"
    versionCode = 1
    versionName = "1.0"

    testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    vectorDrawables.useSupportLibrary = true
  }

  buildTypes {
    debug {
      applicationIdSuffix = ExpeBuildType.DEBUG.applicationIdSuffix
      isDebuggable = true
    }
    val release by getting {
      isMinifyEnabled = true
      isShrinkResources = true
      isDebuggable = false
      proguardFiles(getDefaultProguardFile("proguard-android-optimize.txt"), "proguard-rules.pro")
    }
  }

  compileOptions {
    sourceCompatibility = JavaVersion.VERSION_18
    targetCompatibility = JavaVersion.VERSION_18
  }

  namespace = "com.emendo.expensestracker"
}

dependencies {
  implementation(project(":feature:accounts"))
  implementation(project(":feature:transactions"))
  implementation(project(":feature:categories"))
  implementation(project(":feature:settings"))
  implementation(project(":feature:create-transaction"))

  implementation(project(":core:app-base-ui"))
  implementation(project(":core:common"))
  implementation(project(":core:data"))
  implementation(project(":core:model"))
  implementation(project(":core:designsystem"))
  implementation(project(":core:ui"))

  implementation(project(":sync:work"))

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

  ksp(libs.compose.destinations.ksp)
}
