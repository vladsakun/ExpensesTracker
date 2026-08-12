package com.emendo.expensestracker.core.domain

import com.emendo.expensestracker.core.android.api.OnAppCreate
import com.emendo.expensestracker.core.datastore.ExpePreferencesDataStore
import javax.inject.Inject

class SampleDataSetup @Inject constructor(
  private val preferencesDataStore: ExpePreferencesDataStore,
  private val createSampleAccountAndCategoryUseCase: CreateSampleAccountAndCategoryUseCase,
) : OnAppCreate {

  override suspend fun onCreate() {
    if (!preferencesDataStore.hasCreatedSampleData()) {
      createSampleAccountAndCategoryUseCase()
      preferencesDataStore.setHasCreatedSampleData(true)
    }
  }
}
