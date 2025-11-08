package com.emendo.expensestracker.settings.theme

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.emendo.expensestracker.core.model.data.DarkThemeConfig
import com.emendo.expensestracker.data.api.repository.UserDataRepository
import com.emendo.expensestracker.settings.theme.ThemeUiState.Loading
import com.emendo.expensestracker.settings.theme.ThemeUiState.Success
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted.Companion.WhileSubscribed
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject
import kotlin.time.Duration.Companion.seconds

@HiltViewModel
class ThemeViewModel @Inject constructor(
  private val userDataRepository: UserDataRepository,
) : ViewModel() {
  val themeUiState: StateFlow<ThemeUiState> =
    userDataRepository.userData
      .map { userData ->
        Success(
          settings = UserEditableSettings(
            useDynamicColor = userData.useDynamicColor,
            darkThemeConfig = userData.darkThemeConfig,
          ),
        )
      }
      .stateIn(
        scope = viewModelScope,
        started = WhileSubscribed(5.seconds.inWholeMilliseconds),
        initialValue = Loading,
      )

  fun updateDarkThemeConfig(darkThemeConfig: DarkThemeConfig) {
    viewModelScope.launch {
      userDataRepository.setDarkThemeConfig(darkThemeConfig)
    }
  }

  fun updateDynamicColorPreference(useDynamicColor: Boolean) {
    viewModelScope.launch {
      userDataRepository.setUseDynamicColor(useDynamicColor)
    }
  }
}

/**
 * Represents the settings which the user can edit within the app.
 */
data class UserEditableSettings(
  val useDynamicColor: Boolean,
  val darkThemeConfig: DarkThemeConfig,
)

sealed interface ThemeUiState {
  data object Loading : ThemeUiState
  data class Success(val settings: UserEditableSettings) : ThemeUiState
}
