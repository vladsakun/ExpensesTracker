package com.emendo.expensestracker.core.network.retrofit

import com.emendo.expensestracker.core.model.data.currency.CurrencyModels
import com.emendo.expensestracker.core.network.CurrencyRatesMock
import com.emendo.expensestracker.core.network.CurrencyRatesNetworkDataSource
import com.emendo.expensestracker.core.network.model.CurrencyRates
import kotlinx.datetime.Month
import kotlinx.serialization.json.Json
import okhttp3.Call
import retrofit2.Retrofit
import retrofit2.http.GET
import retrofit2.http.Path
import java.time.Year
import javax.inject.Inject
import javax.inject.Singleton

private const val CURRENCY_RATES_BASE_URL = "https://v6.exchangerate-api.com/v6/6d5c8906bc3ec0bfe7b628f4/"

/**
 * Retrofit API declaration for Currency Network API
 */
interface RetrofitCurrencyNetworkApi {
  @GET("latest/{baseCurrency}")
  suspend fun getCurrencyRates(
    @Path("baseCurrency") baseCurrency: String = CurrencyModels.CURRENCY_RATES_BASE,
  ): CurrencyRates

  @GET("history/{baseCurrency}/{year}/{month}/{day}")
  suspend fun getCurrencyRates(
    @Path("baseCurrency") baseCurrency: String = CurrencyModels.CURRENCY_RATES_BASE,
    @Path("year") year: String,
    @Path("month") month: String,
    @Path("day") day: String,
  ): CurrencyRates
}

/**
 * [Retrofit] backed [CurrencyRatesNetworkDataSource]
 */
@Singleton
class RetrofitCurrencyRatesNetwork @Inject constructor(
  networkJson: Json,
  okhttpCallFactory: Call.Factory,
) : CurrencyRatesNetworkDataSource {

  private val networkApi = CurrencyRatesMock()

  override suspend fun getCurrencyRates(): CurrencyRates = networkApi.getCurrencyRates()

  override suspend fun getCurrencyRatesDate(year: Year, month: Month, day: String): CurrencyRates =
    networkApi.getCurrencyRates(
      year = year.toString(),
      month = month.value.toString(),
      day = day,
    )
}