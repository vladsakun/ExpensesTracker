package com.emendo.expensestracker.core.data.repository.api

import com.emendo.expensestracker.core.app.resources.models.ColorModel
import com.emendo.expensestracker.core.app.resources.models.IconModel
import com.emendo.expensestracker.core.data.model.AccountModel
import com.emendo.expensestracker.core.model.data.CurrencyModel
import kotlinx.coroutines.flow.Flow
import java.math.BigDecimal

interface AccountRepository {
  fun getAccounts(): Flow<List<AccountModel>>
  fun getAccountsSnapshot(): List<AccountModel>
  fun getLastAccount(): Flow<AccountModel?>

  fun getById(id: Long): Flow<AccountModel>

  suspend fun createAccount(
    currency: CurrencyModel,
    name: String,
    icon: IconModel,
    color: ColorModel,
    balance: BigDecimal,
  )

  suspend fun updateAccount(
    id: Long,
    currency: CurrencyModel,
    name: String,
    icon: IconModel,
    color: ColorModel,
    balance: BigDecimal,
  )

  suspend fun updateOrdinalIndex(
    id: Long,
    ordinalIndex: Int,
  )

  suspend fun deleteAccount(id: Long)
}