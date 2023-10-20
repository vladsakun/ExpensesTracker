package com.emendo.expensestracker

enum class ExpeBuildType(val applicationIdSuffix: String? = null) {
  DEBUG(".debug"),
  RELEASE,
  BENCHMARK(".benchmark")
}