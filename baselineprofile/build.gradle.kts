import com.android.build.api.dsl.ManagedVirtualDevice

plugins {
  alias(libs.plugins.android.test)
  alias(libs.plugins.kotlinAndroid)
  alias(libs.plugins.baselineprofile)
}

android {
  namespace = "com.emendo.expensestracker.baselineprofile"
  compileSdk = 34

  compileOptions {
    sourceCompatibility = JavaVersion.VERSION_18
    targetCompatibility = JavaVersion.VERSION_18
  }

  kotlinOptions {
    jvmTarget = JavaVersion.VERSION_18.toString()
  }

  defaultConfig {
    minSdk = 28
    targetSdk = 34

    testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
  }

  targetProjectPath = ":app"

  testOptions.managedDevices.devices {
    create<ManagedVirtualDevice>("pixel6Api34") {
      device = "Pixel 6"
      apiLevel = 34
      systemImageSource = "google"
    }
  }
}

// This is the configuration block for the Baseline Profile plugin.
// You can specify to run the generators on a managed devices or connected devices.
baselineProfile {
  managedDevices += "pixel6Api34"
  useConnectedDevices = false
}

dependencies {
  implementation(libs.junit)
  implementation(libs.androidx.test.espresso.core)
  implementation(libs.androidx.test.uiautomator)
  implementation(libs.androidx.benchmark.macro)
}