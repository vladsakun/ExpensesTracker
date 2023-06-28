plugins {
  id("com.android.application")
  id("org.jetbrains.kotlin.android")
  id(libs.plugins.ksp.get().pluginId) version libs.plugins.ksp.get().version.toString()
}

android {
  compileSdk = 33

  defaultConfig {
    applicationId = "com.emendo.expensestracker"
    minSdk = 26
    targetSdk = 33
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
    kotlinCompilerExtensionVersion = "1.4.7"
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

  implementation(project(":core:designsystem"))

  implementation(libs.androidx.core.ktx)
  implementation(libs.androidx.lifecycle.runtimeCompose)
  implementation(libs.androidx.activity.compose)
  implementation(libs.androidx.core.splashscreen)
  implementation(platform(libs.androidx.compose.bom))
  implementation("androidx.compose.ui:ui")
  implementation("androidx.compose.ui:ui-graphics")
  implementation(libs.androidx.compose.ui.tooling.preview)
  implementation(libs.androidx.compose.material3)
  implementation(libs.compose.destinations)
  implementation(libs.androidx.hilt.navigation.compose)
  implementation(libs.androidx.window.manager)
  implementation(libs.androidx.compose.material3.windowSizeClass)

  ksp(libs.compose.destinations.ksp)

  testImplementation("junit:junit:4.13.2")
  androidTestImplementation("androidx.test.ext:junit:1.1.5")
  androidTestImplementation("androidx.test.espresso:espresso-core:3.5.1")
  androidTestImplementation("androidx.compose.ui:ui-test-junit4")
  debugImplementation("androidx.compose.ui:ui-tooling")
  debugImplementation("androidx.compose.ui:ui-test-manifest")
}
