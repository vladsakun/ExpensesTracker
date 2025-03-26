package com.emendo.expensestracker.core.data

import com.emendo.expensestracker.core.datastore.ChangeListVersions
import com.emendo.expensestracker.core.network.model.NetworkChangeList
import com.emendo.expensestracker.data.api.utils.Synchronizer
import timber.log.Timber
import kotlin.coroutines.cancellation.CancellationException

/**
 * Attempts [block], returning a successful [Result] if it succeeds, otherwise a [Result.Failure]
 * taking care not to break structured concurrency
 */
suspend fun <T> suspendRunCatching(block: suspend () -> T): Result<T> = try {
  Result.success(block())
} catch (cancellationException: CancellationException) {
  throw cancellationException
} catch (exception: Exception) {
  Timber.i(exception, "Failed to evaluate a suspendRunCatchingBlock. Returning failure Result")
  Result.failure(exception)
}

/**
 * Utility function for syncing a repository with the network.
 * [versionReader] Reads the current version of the model that needs to be synced
 * [changeListFetcher] Fetches the change list for the model
 * [versionUpdater] Updates the [ChangeListVersions] after a successful sync
 * [modelDeleter] Deletes models by consuming the ids of the models that have been deleted.
 * [modelUpdater] Updates models by consuming the ids of the models that have changed.
 *
 * Note that the blocks defined above are never run concurrently, and the [Synchronizer]
 * implementation must guarantee this.
 */
suspend fun Synchronizer.changeListSync(
  versionReader: (ChangeListVersions) -> Long,
  changeListFetcher: suspend (Long) -> List<NetworkChangeList>,
  versionUpdater: ChangeListVersions.(Long) -> ChangeListVersions,
  modelDeleter: suspend (List<String>) -> Unit,
  modelUpdater: suspend (List<String>) -> Unit,
) = suspendRunCatching {
  // Fetch the change list since last sync (akin to a git fetch)
  val currentVersion = versionReader(getChangeListVersions())
  val changeList = changeListFetcher(currentVersion)
  if (changeList.isEmpty()) return@suspendRunCatching true

  val (deleted, updated) = changeList.partition(NetworkChangeList::isDelete)

  // Delete models that have been deleted server-side
  modelDeleter(deleted.map(NetworkChangeList::id))

  // Using the change list, pull down and save the changes (akin to a git pull)
  modelUpdater(updated.map(NetworkChangeList::id))

  // Update the last synced version (akin to updating local git HEAD)
  val latestVersion = changeList.last().changeListVersion
  updateChangeListVersions {
    versionUpdater(latestVersion)
  }
}.isSuccess