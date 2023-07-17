import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
  `kotlin-dsl`
}

group = "com.emendo.expensestracker.buildlogic"

java {
  sourceCompatibility = JavaVersion.VERSION_18
  targetCompatibility = JavaVersion.VERSION_18
}

tasks.withType<KotlinCompile>().configureEach {
  kotlinOptions {
    jvmTarget = JavaVersion.VERSION_18.toString()
  }
}

dependencies {
  compileOnly(libs.android.gradlePlugin)
  compileOnly(libs.kotlin.gradlePlugin)
  compileOnly(libs.ksp.gradlePlugin)
}

gradlePlugin {
  plugins {
    register("androidApplicationCompose"){
      id = "expensestracker.android.application.compose"
      implementationClass = "AndroidApplicationComposeConventionPlugin"
    }
    register("androidApplication"){
      id = "expensestracker.android.application"
      implementationClass = "AndroidApplicationConventionPlugin"
    }
    register("androidLibraryCompose"){
      id = "expensestracker.android.library.compose"
      implementationClass = "AndroidLibraryComposeConventionPlugin"
    }
    register("androidLibrary"){
      id = "expensestracker.android.library"
      implementationClass = "AndroidLibraryConventionPlugin"
    }
    register("androidFeature") {
      id = "expensestracker.android.feature"
      implementationClass = "AndroidFeatureConventionPlugin"
    }
    register("androidHilt") {
      id = "expensestracker.android.hilt"
      implementationClass = "AndroidHiltConventionPlugin"
    }
    register("androidRoom") {
      id = "expensestracker.android.room"
      implementationClass = "AndroidRoomConventionPlugin"
    }
    register("jvmLibrary"){
      id = "expensestracker.jvm.library"
      implementationClass = "JvmLibraryConventionPlugin"
    }
  }
}