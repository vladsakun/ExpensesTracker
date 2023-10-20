package com.emendo.expensestracker.core.app.common.network

import javax.inject.Qualifier
import kotlin.annotation.AnnotationRetention.RUNTIME

@Qualifier
@Retention(RUNTIME)
annotation class Dispatcher(val expeDispatcher: ExpeDispatchers)

enum class ExpeDispatchers {
  Default,
  IO,
}