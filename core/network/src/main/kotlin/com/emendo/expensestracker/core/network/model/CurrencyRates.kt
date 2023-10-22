package com.emendo.expensestracker.core.network.model

import kotlinx.serialization.Serializable

// Todo remove serialization
@Serializable
data class CurrencyRates(
    val base_code: String,
    val documentation: String,
    val provider: String,
    val rates: Map<String, Double>,
    val result: String,
    val time_eol_unix: Int,
    val time_last_update_unix: Int,
    val time_last_update_utc: String,
    val time_next_update_unix: Int,
    val time_next_update_utc: String,
)