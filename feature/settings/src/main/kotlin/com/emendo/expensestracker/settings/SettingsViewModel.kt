package com.emendo.expensestracker.settings

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.emendo.expensestracker.core.app.common.network.Dispatcher
import com.emendo.expensestracker.core.app.common.network.ExpeDispatchers
import com.emendo.expensestracker.core.app.resources.icon.ExpeIcons
import com.emendo.expensestracker.core.data.repository.api.UserDataRepository
import com.emendo.expensestracker.core.model.data.UserData
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.collections.immutable.persistentListOf
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject
import com.emendo.expensestracker.core.app.resources.R as AppR

@HiltViewModel
class SettingsViewModel @Inject constructor(
  private val userDataRepository: UserDataRepository,
  @Dispatcher(ExpeDispatchers.IO) private val ioDispatcher: CoroutineDispatcher,
) : ViewModel() {

  val state = getSettingsScreenData(userDataRepository, ::mapState)
    .stateIn(
      scope = viewModelScope,
      started = SharingStarted.WhileSubscribed(5_000L),
      initialValue = mapState(userDataRepository.getUserDataSnapshot())
    )

  private fun mapState(userData: UserData? = null): SettingsScreenData =
    SettingsScreenData(
      settingsItems = persistentListOf(
        SettingsItemModel(
          id = GENERAL_CURRENCY_ID,
          titleResId = AppR.string.general_currency,
          value = userData?.generalCurrencyCode?.let(SettingsItemValue::StringValue),
          icon = ExpeIcons.Currency,
        ),
        SettingsItemModel(
          id = REMINDERS,
          titleResId = AppR.string.reminders,
          value = userData?.shouldShowNotifications?.let {
            SettingsItemValue.StringResValue(getSettingsItemStringRes(it))
          },
          icon = ExpeIcons.Notifications,
        ),
        SettingsItemModel(
          id = APPEARANCE,
          titleResId = AppR.string.appearance,
          icon = ExpeIcons.ColorLens,
        ),
        SettingsItemModel(
          id = SECURITY,
          titleResId = AppR.string.security,
          icon = ExpeIcons.Security,
        ),
        SettingsItemModel(
          id = BACKUPS,
          titleResId = AppR.string.backups,
          icon = ExpeIcons.Backup,
        ),
        SettingsItemModel(
          id = HELP,
          titleResId = AppR.string.help,
          icon = ExpeIcons.Help,
        ),
        SettingsItemModel(
          id = ADVANCED_SETTINGS,
          titleResId = AppR.string.advanced_settings,
          icon = ExpeIcons.AdvancedSettings,
        ),
      ),
    )

  private fun getSettingsItemStringRes(it: Boolean) =
    if (it) AppR.string.on_label else AppR.string.off_label

  fun onItemClick(settingsItemModel: SettingsItemModel) {
    viewModelScope.launch(ioDispatcher) {
      when (settingsItemModel.id) {
        GENERAL_CURRENCY_ID -> {
          if (userDataRepository.getGeneralCurrencyCode() == "USD") {
            userDataRepository.setGeneralCurrencyCode("EUR")
          } else {
            userDataRepository.setGeneralCurrencyCode("USD")
          }
        }
      }
    }
  }

  companion object {
    const val GENERAL_CURRENCY_ID = 1
    const val REMINDERS = 2
    const val APPEARANCE = 3
    const val SECURITY = 4
    const val BACKUPS = 5
    const val HELP = 6
    const val ADVANCED_SETTINGS = 7
  }
}

private fun getSettingsScreenData(
  userDataRepository: UserDataRepository,
  mapState: (UserData?) -> SettingsScreenData,
) = userDataRepository.userData.map(mapState)