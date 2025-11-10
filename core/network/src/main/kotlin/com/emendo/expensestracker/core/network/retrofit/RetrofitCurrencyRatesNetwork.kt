package com.emendo.expensestracker.core.network.retrofit

import com.emendo.expensestracker.core.model.data.currency.CurrencyModels
import com.emendo.expensestracker.core.network.CurrencyRatesNetworkDataSource
import com.emendo.expensestracker.core.network.model.CurrencyRates
import com.jakewharton.retrofit2.converter.kotlinx.serialization.asConverterFactory
import kotlinx.datetime.Month
import kotlinx.serialization.json.Json
import okhttp3.Call
import okhttp3.MediaType.Companion.toMediaType
import retrofit2.Retrofit
import retrofit2.http.GET
import retrofit2.http.Path
import java.time.Year
import javax.inject.Inject
import javax.inject.Singleton

private const val CURRENCY_RATES_BASE_URL = "https://open.er-api.com/v6/latest/"

/**
 * Retrofit API declaration for Currency Network API
 */
private interface RetrofitCurrencyNetworkApi {
  @GET("{baseCurrency}")
  suspend fun getCurrencyRates(
    @Path("baseCurrency") baseCurrency: String = CurrencyModels.CURRENCY_RATES_BASE,
  ): CurrencyRates

  @GET("{baseCurrency}/{year}/{month}/{day}")
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

  private val networkApi = Retrofit.Builder()
    .baseUrl(CURRENCY_RATES_BASE_URL)
    .callFactory(okhttpCallFactory)
    .addConverterFactory(networkJson.asConverterFactory("application/json".toMediaType()))
    .build()
    .create(RetrofitCurrencyNetworkApi::class.java)

  override suspend fun getCurrencyRates(): CurrencyRates = networkApi.getCurrencyRates()

  override suspend fun getCurrencyRatesDate(year: Year, month: Month, day: String): CurrencyRates =
    networkApi.getCurrencyRates(
      year = year.toString(),
      month = month.value.toString(),
      day = day,
    )
}