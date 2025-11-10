package com.emendo.expensestracker

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.emendo.expensestracker.core.app.common.ext.stateInWhileSubscribed
import com.emendo.expensestracker.core.model.data.DarkThemeConfig
import com.emendo.expensestracker.core.model.data.UserData
import com.emendo.expensestracker.data.api.manager.ExpeDateManager
import com.emendo.expensestracker.data.api.manager.ExpeLocaleManager
import com.emendo.expensestracker.data.api.manager.ExpeTimeZoneManager
import com.emendo.expensestracker.data.api.repository.UserDataRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

@HiltViewModel
class MainActivityViewModel @Inject constructor(
  private val localeManager: ExpeLocaleManager,
  private val timeZoneManager: ExpeTimeZoneManager,
  private val dateManager: ExpeDateManager,
  userDataRepository: UserDataRepository,
) : ViewModel() {

  val uiState: StateFlow<MainActivityUiState> = userDataRepository.userData.map {
    MainActivityUiState.Success(it)
  }.stateInWhileSubscribed(scope = viewModelScope, initialValue = MainActivityUiState.Loading)

  fun updateLocale() {
    localeManager.onLocaleChange()
  }

  fun updateTimeZone() {
    timeZoneManager.onZoneChange()
  }

  fun updateDate() {
    dateManager.onDateChange()
  }
}

sealed interface MainActivityUiState {
  data object Loading : MainActivityUiState

  data class Success(val userData: UserData) : MainActivityUiState {
    override val shouldDisableDynamicTheming = !userData.useDynamicColor

    override fun shouldUseDarkTheme(isSystemDarkTheme: Boolean) =
      when (userData.darkThemeConfig) {
        DarkThemeConfig.FOLLOW_SYSTEM -> isSystemDarkTheme
        DarkThemeConfig.LIGHT -> false
        DarkThemeConfig.DARK -> true
      }
  }

  /**
   * Returns `true` if the state wasn't loaded yet and it should keep showing the splash screen.
   */
  fun shouldKeepSplashScreen() = this is Loading

  /**
   * Returns `true` if the dynamic color is disabled.
   */
  val shouldDisableDynamicTheming: Boolean get() = true

  /**
   * Returns `true` if dark theme should be used.
   */
  fun shouldUseDarkTheme(isSystemDarkTheme: Boolean) = isSystemDarkTheme
}