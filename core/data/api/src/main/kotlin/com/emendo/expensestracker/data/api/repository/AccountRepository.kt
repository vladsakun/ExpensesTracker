package com.emendo.expensestracker.data.api.repository

import com.emendo.expensestracker.core.app.resources.models.IconModel
import com.emendo.expensestracker.core.model.data.AccountWithOrdinalIndex
import com.emendo.expensestracker.core.model.data.currency.CurrencyModel
import com.emendo.expensestracker.data.api.model.AccountModel
import kotlinx.coroutines.flow.Flow
import java.math.BigDecimal

interface AccountRepository {
  fun getAccounts(): Flow<List<AccountModel>>
  fun getAccountsSnapshot(): List<AccountModel>
  fun getLastAccount(): Flow<AccountModel?>

  fun getById(id: Long): Flow<AccountModel>
  fun getByIdSnapshot(id: Long): AccountModel?

  suspend fun createAccount(
    currency: CurrencyModel,
    name: String,
    icon: IconModel,
    color: com.emendo.expensestracker.model.ui.ColorModel,
    balance: BigDecimal,
  )

  suspend fun updateAccount(
    id: Long,
    currency: CurrencyModel,
    name: String,
    icon: IconModel,
    color: com.emendo.expensestracker.model.ui.ColorModel,
    balance: BigDecimal,
  )

  suspend fun updateOrdinalIndex(newOrderedList: Set<AccountWithOrdinalIndex>)

  suspend fun deleteAccount(id: Long)
}