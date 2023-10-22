package com.emendo.expensestracker.core.datastore

import kotlinx.datetime.Instant

/**
 * Class summarizing the local version of each model for sync
 */
data class ChangeListVersions(
  val currencyRatesLastUpdateInstant: Instant = Instant.DISTANT_PAST,
)