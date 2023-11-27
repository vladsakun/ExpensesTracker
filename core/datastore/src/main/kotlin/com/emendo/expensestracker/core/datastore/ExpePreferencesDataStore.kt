package com.emendo.expensestracker.core.datastore

import androidx.datastore.core.DataStore
import com.emendo.expensestracker.core.model.data.CurrencyModels
import com.emendo.expensestracker.core.model.data.UserData
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.map
import kotlinx.datetime.Instant
import timber.log.Timber
import java.io.IOException
import javax.inject.Inject

class ExpePreferencesDataStore @Inject constructor(
  private val userPreferences: DataStore<UserPreferences>,
) {
  val userData: Flow<UserData> = userPreferences.data
    .map {
      UserData(
        useDynamicColor = it.useDynamicColor,
        generalCurrencyCode = it.generalCurrencyCode,
        shouldShowNotifications = it.shouldShowNotifications,
        isBackupEnabled = it.isBackupEnabled,
      )
    }

  val generalCurrencyCode: Flow<String> = userPreferences.data
    .map { it.generalCurrencyCode }

  val favouriteCurrenciesCodes: Flow<Set<String>> = userPreferences.data
    .map { it.favouriteCurrencyCodesMap.keys }

  suspend fun shouldUseDynamicColor(): Boolean =
    userData.firstOrNull()?.useDynamicColor ?: false

  suspend fun setUseDynamicColor(useDynamicColor: Boolean) {
    userPreferences.updateData {
      it.copy {
        this.useDynamicColor = useDynamicColor
      }
    }
  }

  suspend fun getGeneralCurrencyCode(): String =
    userPreferences.data.map { it.generalCurrencyCode }.firstOrNull() ?: CurrencyModels.localCurrency.currencyCode

  suspend fun setGeneralCurrencyCode(generalCurrencyCode: String) {
    require(generalCurrencyCode.length == 3) { "Currency code length must be 3" }

    userPreferences.updateData {
      it.copy {
        this.generalCurrencyCode = generalCurrencyCode
      }
    }
  }

  suspend fun getFavouriteCurrenciesCodes(): Set<String> =
    userPreferences.data.map { it.favouriteCurrencyCodesMap.keys }.firstOrNull() ?: emptySet()

  suspend fun addFavouriteCurrencyCode(currencyCode: String) {
    userPreferences.updateData {
      it.copy {
        favouriteCurrencyCodes.put(currencyCode, true)
      }
    }
  }

  suspend fun getChangeListVersions() = userPreferences.data
    .map {
      val instant = it.getCurrencyRatesChangeInstant()
      ChangeListVersions(
        currencyRatesLastUpdateInstant = instant
      )
    }
    .firstOrNull() ?: ChangeListVersions()

  /**
   * Update the [ChangeListVersions] using [update].
   */
  suspend fun updateChangeListVersion(update: ChangeListVersions.() -> ChangeListVersions) {
    try {
      userPreferences.updateData { currentPreferences ->
        val updatedChangeListVersions = update(
          ChangeListVersions(
            currencyRatesLastUpdateInstant = currentPreferences.getCurrencyRatesChangeInstant(),
          ),
        )

        currentPreferences.copy {
          currencyRatesChangeListInstantSeconds =
            updatedChangeListVersions.currencyRatesLastUpdateInstant?.epochSeconds ?: 0L
        }
      }
    } catch (ioException: IOException) {
      Timber.e(ioException, "Failed to update user preferences")
    }
  }

  private fun UserPreferences.getCurrencyRatesChangeInstant(): Instant? {
    if (currencyRatesChangeListInstantSeconds == 0L) {
      return null
    }

    return Instant.fromEpochSeconds(currencyRatesChangeListInstantSeconds)
  }
}