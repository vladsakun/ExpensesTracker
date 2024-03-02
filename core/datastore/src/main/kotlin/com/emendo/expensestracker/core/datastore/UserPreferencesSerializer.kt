package com.emendo.expensestracker.core.datastore

import androidx.datastore.core.CorruptionException
import androidx.datastore.core.Serializer
import com.emendo.expensestracker.core.model.data.currency.CurrencyModels
import com.google.protobuf.InvalidProtocolBufferException
import java.io.InputStream
import java.io.OutputStream
import javax.inject.Inject

class UserPreferencesSerializer @Inject constructor() : Serializer<UserPreferences> {
  override val defaultValue: UserPreferences = UserPreferences.getDefaultInstance()
    .toBuilder()
    .setGeneralCurrencyCode(CurrencyModels.localCurrency.currencyCode)
    .build()

  override suspend fun readFrom(input: InputStream): UserPreferences =
    try {
      UserPreferences.parseFrom(input)
    } catch (exception: InvalidProtocolBufferException) {
      throw CorruptionException("Cannot read proto.", exception)
    }

  override suspend fun writeTo(t: UserPreferences, output: OutputStream) {
    t.writeTo(output)
  }
}