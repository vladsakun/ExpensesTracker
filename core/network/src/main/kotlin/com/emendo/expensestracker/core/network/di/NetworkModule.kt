package com.emendo.expensestracker.core.network.di

import android.content.Context
import com.emendo.expensestracker.core.network.BuildConfig
import com.emendo.expensestracker.core.network.CurrencyRatesNetworkDataSource
import com.emendo.expensestracker.core.network.fake.FakeAssetManager
import com.emendo.expensestracker.core.network.retrofit.RetrofitCurrencyRatesNetwork
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import kotlinx.serialization.json.Json
import okhttp3.Call
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class NetworkModule {

  // Todo remove binds (in now in android they don't have it)
  @Binds
  @Singleton
  abstract fun bindsCurrencyRatesNetworkDataSource(network: RetrofitCurrencyRatesNetwork): CurrencyRatesNetworkDataSource

  companion object {
    @Provides
    @Singleton
    fun providesNetworkJson(): Json = Json {
      ignoreUnknownKeys = true
    }

    @Provides
    @Singleton
    fun providesFakeAssetManager(
      @ApplicationContext context: Context,
    ): FakeAssetManager = FakeAssetManager(context.assets::open)

    @Provides
    @Singleton
    fun okHttpCallFactory(): Call.Factory = OkHttpClient.Builder()
      .addInterceptor(
        HttpLoggingInterceptor()
          .apply {
            if (BuildConfig.DEBUG) {
              setLevel(HttpLoggingInterceptor.Level.BODY)
            }
          },
      )
      .build()
  }
}