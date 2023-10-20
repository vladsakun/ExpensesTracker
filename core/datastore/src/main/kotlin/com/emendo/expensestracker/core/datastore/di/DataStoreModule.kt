package com.emendo.expensestracker.core.datastore.di

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.core.DataStoreFactory
import androidx.datastore.dataStoreFile
import com.emendo.expensestracker.core.app.common.network.Dispatcher
import com.emendo.expensestracker.core.app.common.network.ExpeDispatchers
import com.emendo.expensestracker.core.app.common.network.di.ApplicationScope
import com.emendo.expensestracker.core.datastore.UserPreferences
import com.emendo.expensestracker.core.datastore.UserPreferencesSerializer
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DataStoreModule {

  @Provides
  @Singleton
  fun providesUserPreferencesDataStore(
    @ApplicationContext context: Context,
    @Dispatcher(ExpeDispatchers.IO) ioDispatcher: CoroutineDispatcher,
    @ApplicationScope scope: CoroutineScope,
    userPreferencesSerializer: UserPreferencesSerializer,
  ): DataStore<UserPreferences> =
    DataStoreFactory.create(
      serializer = userPreferencesSerializer,
      scope = CoroutineScope(scope.coroutineContext + ioDispatcher),
    ) {
      context.dataStoreFile("user_preferences.pb")
    }
}