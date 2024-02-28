package com.emendo.expensestracker.core.domain.common

import kotlinx.coroutines.flow.Flow

// I know it is kinda miss use of Decorator pattern, but I just wanted to try it out 🤡
interface GetModelComponent<T> {
  operator fun invoke(id: Long): Flow<T>
}