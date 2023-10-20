package com.emendo.expensestracker.core.data.manager

import javax.inject.Inject

class AppInitManagerImpl @Inject constructor() : AppInitManager {

  override suspend fun init() {}
}