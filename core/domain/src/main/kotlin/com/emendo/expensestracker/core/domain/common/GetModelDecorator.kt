package com.emendo.expensestracker.core.domain.common

import kotlinx.coroutines.flow.Flow

interface GetModelDecorator<T> : GetModelComponent<T> {
  val getModelComponent: GetModelComponent<T>

  override operator fun invoke(id: Long): Flow<T>
}