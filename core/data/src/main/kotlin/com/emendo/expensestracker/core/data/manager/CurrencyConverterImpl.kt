package com.emendo.expensestracker.core.data.manager

import com.emendo.expensestracker.core.data.applyDefaultDecimalStyle
import com.emendo.expensestracker.core.data.divideWithScale
import com.emendo.expensestracker.core.model.data.currency.CurrencyModels
import com.emendo.expensestracker.core.model.data.exception.CurrencyRateNotFoundException
import com.emendo.expensestracker.data.api.manager.CurrencyConverter
import com.emendo.expensestracker.data.api.model.CurrencyRateModel
import java.math.BigDecimal
import javax.inject.Inject

class CurrencyConverterImpl @Inject constructor() : CurrencyConverter {

  override fun convert(
    value: BigDecimal,
    fromCurrencyCode: String,
    toCurrencyCode: String,
    usdToOriginalRate: BigDecimal,
    currencyRates: Map<String, CurrencyRateModel>,
  ): BigDecimal {
    if (fromCurrencyCode == toCurrencyCode) {
      return value
    }

    // Курс В: Получаем из Map (это будет курс USD -> ToCurrency)
    val toRate = currencyRates[toCurrencyCode]?.rate ?: throw CurrencyRateNotFoundException()

    // 2. Валюта-основа (USD) всегда равна 1.00.
    // Если исходная валюта - USD, просто умножаем на целевой курс.
    if (fromCurrencyCode == CurrencyModels.CURRENCY_RATES_BASE) {
      return (value * toRate).applyDefaultDecimalStyle()
    }

    // 3. Получение курсов:

    // Курс ИЗ: Используем исторический "якорь" из транзакции
    val fromRate = usdToOriginalRate

    // 4. Логика конвертации (через USD):

    // A. Конвертация в Базовую Валюту (USD)
    // Формула: Amount_USD = Amount_Original / Rate_USD_to_Original
    val valueInBase = value.divideWithScale(fromRate)

    // B. Конвертация из Базовой Валюты (USD) в Целевую (EUR, CZK и т.д.)
    // Формула: Amount_Target = Amount_USD * Rate_USD_to_Target
    return valueInBase.multiply(toRate).applyDefaultDecimalStyle()
  }

  override fun convert(
    value: BigDecimal,
    fromCurrencyCode: String,
    toCurrencyCode: String,
    currencyRates: Map<String, CurrencyRateModel>,
  ): BigDecimal {
    if (fromCurrencyCode == toCurrencyCode) {
      return value
    }

    val toRate = currencyRates[toCurrencyCode]?.rate ?: throw CurrencyRateNotFoundException()
    if (fromCurrencyCode == CurrencyModels.CURRENCY_RATES_BASE) {
      return (value * toRate).applyDefaultDecimalStyle()
    }

    val fromRate = currencyRates[fromCurrencyCode]?.rate ?: throw CurrencyRateNotFoundException()
    if (toCurrencyCode == CurrencyModels.CURRENCY_RATES_BASE) {
      return value.divideWithScale(fromRate)
    }

    return value.divideWithScale(fromRate).multiply(toRate).applyDefaultDecimalStyle()
  }
}