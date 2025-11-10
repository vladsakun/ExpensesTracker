package com.emendo.expensestracker.settings

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.emendo.expensestracker.app.base.api.screens.SelectCurrencyScreenApi
import com.emendo.expensestracker.core.app.common.ext.stateInWhileSubscribed
import com.emendo.expensestracker.core.app.common.network.Dispatcher
import com.emendo.expensestracker.core.app.common.network.ExpeDispatchers
import com.emendo.expensestracker.core.app.resources.icon.ExpeIcons
import com.emendo.expensestracker.core.model.data.UserData
import com.emendo.expensestracker.data.api.repository.UserDataRepository
import com.emendo.expensestracker.model.ui.textValueOf
import com.emendo.expensestracker.settings.destinations.HelpArticlesListScreenDestination
import dagger.hilt.android.lifecycle.HiltViewModel
import de.palm.composestateevents.StateEvent
import kotlinx.collections.immutable.persistentListOf
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject
import com.emendo.expensestracker.app.resources.R as AppR

@HiltViewModel
class SettingsViewModel
@Inject
constructor(
  private val userDataRepository: UserDataRepository,
  @Dispatcher(ExpeDispatchers.IO) private val ioDispatcher: CoroutineDispatcher,
  @Dispatcher(ExpeDispatchers.Default) private val defaultDispatcher: CoroutineDispatcher,
  private val selectCurrencyScreenApi: SelectCurrencyScreenApi,
) : ViewModel() {

  private val showThemeDialog: MutableStateFlow<StateEvent> = MutableStateFlow(StateEvent.Consumed)

  internal val state = getSettingsScreenData(userDataRepository, showThemeDialog, ::mapState)
    .stateInWhileSubscribed(scope = viewModelScope, initialValue = mapState(userDataRepository.getUserDataSnapshot()))

  private val navigationChannel: Channel<String> = Channel(Channel.CONFLATED)
  internal val navigationEvent = navigationChannel.receiveAsFlow()

  private fun mapState(userData: UserData?): SettingsScreenData =
    SettingsScreenData(
      settingsItems =
        persistentListOf(
          SettingsItemModel(
            id = GENERAL_CURRENCY_ID,
            titleResId = AppR.string.general_currency,
            value = textValueOf(userData?.generalCurrencyCode ?: ""),
            icon = ExpeIcons.Currency,
          ),
          //        SettingsItemModel(
          //          id = REMINDERS,
          //          titleResId = AppR.string.reminders,
          //          value =
          //          userData?.shouldShowNotifications?.let {
          //            SettingsItemValue.StringResValue(getSettingsItemStringRes(it))
          //          },
          //          icon = ExpeIcons.Notifications,
          //        ),
          SettingsItemModel(
            id = APPEARANCE,
            titleResId = AppR.string.appearance,
            icon = ExpeIcons.ColorLens,
          ),
          //        SettingsItemModel(
          //          id = SECURITY,
          //          titleResId = AppR.string.security,
          //          icon = ExpeIcons.Security,
          //        ),
          //        SettingsItemModel(
          //          id = BACKUPS,
          //          titleResId = AppR.string.backups,
          //          icon = ExpeIcons.Backup,
          //        ),
          SettingsItemModel(
            id = HELP,
            titleResId = AppR.string.help,
            icon = ExpeIcons.Help,
          ),
          //        SettingsItemModel(
          //          id = ADVANCED_SETTINGS,
          //          titleResId = AppR.string.advanced_settings,
          //          icon = ExpeIcons.AdvancedSettings,
          //        ),
        ),
    )

  private fun getSelectCurrencyScreenRoute(): String =
    selectCurrencyScreenApi.getSelectCurrencyScreenRoute()

  fun onItemClick(settingsItemModel: SettingsItemModel) {
    when (settingsItemModel.id) {
      GENERAL_CURRENCY_ID -> {
        navigationChannel.trySend(getSelectCurrencyScreenRoute())
      }
      HELP -> {
        navigationChannel.trySend(HelpArticlesListScreenDestination.route)
      }
      APPEARANCE -> {
        showThemeDialog.value = StateEvent.Triggered
      }
    }
  }

  fun updateCurrencyByCode(currency: String) {
    viewModelScope.launch(defaultDispatcher) {
      userDataRepository.setGeneralCurrencyCode(currency)
    }
  }

  fun dismissThemeDialog() {
    showThemeDialog.value = StateEvent.Consumed
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
  showThemeDialog: StateFlow<StateEvent>,
  mapState: (UserData?) -> SettingsScreenData,
): Flow<SettingsScreenData> = combine(userDataRepository.userData, showThemeDialog) { userData, showThemeDialogValue ->
  mapState(userData).copy(showThemeDialog = showThemeDialogValue)
}
