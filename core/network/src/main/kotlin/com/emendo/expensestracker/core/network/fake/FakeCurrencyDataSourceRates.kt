package com.emendo.expensestracker.core.network.fake

import JvmUnitTestFakeAssetManager
import com.emendo.expensestracker.core.app.common.network.Dispatcher
import com.emendo.expensestracker.core.app.common.network.ExpeDispatchers
import com.emendo.expensestracker.core.network.CurrencyRatesNetworkDataSource
import com.emendo.expensestracker.core.network.model.CurrencyRates
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.withContext
import kotlinx.datetime.Month
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.decodeFromStream
import okio.use
import java.time.Year
import javax.inject.Inject

class FakeCurrencyDataSourceRates @Inject constructor(
  @Dispatcher(ExpeDispatchers.IO) private val ioDispatcher: CoroutineDispatcher,
  private val networkJson: Json,
  private val assets: FakeAssetManager = JvmUnitTestFakeAssetManager,
) : CurrencyRatesNetworkDataSource {

  @OptIn(ExperimentalSerializationApi::class)
  override suspend fun getCurrencyRates(): CurrencyRates =
    withContext(ioDispatcher) {
      assets.open(CURRENCY_RATES_ASSET).use(networkJson::decodeFromStream)
    }

  override suspend fun getCurrencyRatesDate(year: Year, month: Month, day: String): CurrencyRates {
    return getCurrencyRates()
  }

  companion object {
    private const val CURRENCY_RATES_ASSET = "currency_rates.json"
  }
}