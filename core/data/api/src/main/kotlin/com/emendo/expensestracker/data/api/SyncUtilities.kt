package com.emendo.expensestracker.data.api

import com.emendo.expensestracker.core.datastore.ChangeListVersions

interface Synchronizer {
  suspend fun getChangeListVersions(): ChangeListVersions

  suspend fun updateChangeListVersions(update: ChangeListVersions.() -> ChangeListVersions)

  /**
   * Syntactic sugar to call [Syncable.syncWith] while omitting the synchronizer argument
   */
  suspend fun Syncable.sync() = this@sync.syncWith(this@Synchronizer)
}

/**
 * Interface marker for a class that is synchronized with a remote source. Syncing must not be
 * performed concurrently and it is the [Synchronizer]'s responsibility to ensure this.
 */
interface Syncable {
  /**
   * Synchronizes the local database backing the repository with the network.
   * Returns if the sync was successful or not.
   */
  suspend fun syncWith(synchronizer: Synchronizer): Boolean
}