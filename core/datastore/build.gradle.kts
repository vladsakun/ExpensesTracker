plugins {
  alias(libs.plugins.expensestracker.android.library)
  alias(libs.plugins.expensestracker.android.hilt)
  alias(libs.plugins.protobuf)
}

android {
  defaultConfig {
    consumerProguardFile("consumer-proguard-rules.pro")
  }
  namespace = "com.emendo.expensestracker.core.datastore"
}

protobuf {
  protoc {
    artifact = libs.protobuf.protoc.get().toString()
  }
  generateProtoTasks {
    all().forEach { task ->
      task.builtins {
        register("java") {
          option("lite")
        }
        register("kotlin") {
          option("lite")
        }
      }
    }
  }
}

androidComponents.beforeVariants {
  android.sourceSets.getByName(it.name) {
    java.srcDirs(buildDir.resolve("generated/source/proto/${it.name}/java"))
    kotlin.srcDirs(buildDir.resolve("generated/source/proto/${it.name}/kotlin"))
  }
}

dependencies {
  implementation(projects.core.common)
  implementation(projects.core.model)

  implementation(libs.androidx.dataStore.core)
  implementation(libs.kotlinx.coroutines.android)
  implementation(libs.kotlinx.datetime)
  implementation(libs.protobuf.kotlin.lite)
}