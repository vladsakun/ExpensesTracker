plugins {
  id("expensestracker.android.application")
  id("expensestracker.android.application.compose")
  id("expensestracker.android.hilt")
}

android {
  compileSdk = libs.versions.compileSdk.get().toInt()

  defaultConfig {
    applicationId = "com.emendo.expensestracker"
    minSdk = libs.versions.minSdk.get().toInt()
    targetSdk = libs.versions.targetSdk.get().toInt()
    versionCode = 1
    versionName = "1.0"

    testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    vectorDrawables.useSupportLibrary = true
  }

  buildTypes {
    release {
      isMinifyEnabled = false
      proguardFiles(getDefaultProguardFile("proguard-android-optimize.txt"), "proguard-rules.pro")
    }
  }

  compileOptions {
    sourceCompatibility = JavaVersion.VERSION_18
    targetCompatibility = JavaVersion.VERSION_18
  }

  kotlinOptions {
    jvmTarget = "18"
  }

  buildFeatures {
    compose = true
  }

  composeOptions {
    kotlinCompilerExtensionVersion = libs.versions.androidxComposeCompiler.get()
  }

  packagingOptions {
    resources {
      excludes.add("/META-INF/{AL2.0,LGPL2.1}")
    }
  }
  namespace = "com.emendo.expensestracker"
}

dependencies {
  implementation(project(":feature:accounts"))
  implementation(project(":feature:transactions"))
  implementation(project(":feature:categories"))

  implementation(project(":core:common"))
  implementation(project(":core:data"))
  implementation(project(":core:model"))
  implementation(project(":core:designsystem"))

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

  testImplementation("junit:junit:4.13.2")
  androidTestImplementation("androidx.test.ext:junit:1.1.5")
  androidTestImplementation("androidx.test.espresso:espresso-core:3.5.1")
  androidTestImplementation("androidx.compose.ui:ui-test-junit4")
  debugImplementation("androidx.compose.ui:ui-tooling")
  debugImplementation("androidx.compose.ui:ui-test-manifest")
}
