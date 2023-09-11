package com.emendo.expensestracker.core.data.amount

import android.os.Parcelable
import kotlinx.parcelize.Parcelize
import java.math.BigDecimal
import java.math.BigInteger
import kotlin.math.abs
import kotlin.math.max
import kotlin.math.pow

@Parcelize
data class Amount(
  var value: Long,
  val precision: Int,
) : Parcelable, Comparable<Amount> {

  fun isZero() = value == 0L

  fun isPositive() = 0L < value

  fun isNegative() = value < 0L

  fun toBigDecimal(): BigDecimal = BigDecimal(value.toBigInteger(), precision)

  /** Convert the amount to long value resolving the precision (NOTE: decimal places are lost) */
  fun getValueWithoutPrecision() = value / TEN.pow(precision).toLong()

  fun getValueWithPrecision() = value / TEN.pow(precision.toDouble())

  override fun compareTo(other: Amount): Int {
    return toBigDecimal().compareTo(other.toBigDecimal())
  }

  operator fun unaryMinus(): Amount = copy(value = -value)

  // Note: decrement null is considered to be zero
  // Note: if precision values are not equal result amount has bigger precision value
  operator fun plus(increment: Amount?): Amount =
    increment?.let { incAmount ->
      val originalValue = value.toBigDecimal().divide(BigDecimal.TEN.pow(precision))
      val incrementValue = incAmount.value.toBigDecimal().divide(BigDecimal.TEN.pow(incAmount.precision))
      val resultPrecision = max(precision, incAmount.precision)
      val resultValue = (originalValue + incrementValue) * BigDecimal.TEN.pow(resultPrecision)
      return Amount(resultValue.toLong(), resultPrecision)
    } ?: this

  // Note: decrement null is considered to be zero
  // Note: if precision values are not equal result amount has bigger precision value
  operator fun minus(decrement: Amount?): Amount =
    decrement?.let { decAmount ->
      val originalValue = value.toBigDecimal().divide(BigDecimal.TEN.pow(precision))
      val decrementValue = decAmount.value.toBigDecimal().divide(BigDecimal.TEN.pow(decAmount.precision))
      val resultPrecision = max(precision, decAmount.precision)
      val resultValue = (originalValue - decrementValue) * BigDecimal.TEN.pow(resultPrecision)
      return Amount(resultValue.toLong(), resultPrecision)
    } ?: this

  fun abs(): Amount = copy(value = abs(value))

  override fun equals(other: Any?): Boolean = when (other) {
    is Amount -> {
      var thisValue = value
      var comparatorValue = other.value
      if (precision > other.precision) {
        comparatorValue = other.value * TEN.pow(precision - other.precision).toLong()
      } else if (precision < other.precision) {
        thisValue = value * TEN.pow(other.precision - precision).toLong()
      }
      thisValue == comparatorValue
    }

    else -> false
  }

  override fun hashCode(): Int {
    val shift = HASH_CODE_PRECISION - precision
    val decimal = BigDecimal(value.toBigInteger() * BigInteger.TEN.pow(shift), HASH_CODE_PRECISION)

    var result = decimal.hashCode()
    result *= 31
    return result
  }

  companion object {
    private val HUNDRED = BigDecimal(100)
    private const val TEN = 10.0
    private const val HASH_CODE_PRECISION = 20

    /**
     * Creates an [Amount] from a [BigDecimal] and a [currency].
     * @param scale Precision of the returned [Amount]. Default is scale of the [BigDecimal].
     * @throws ArithmeticException when [scale] is too small and [value] would have to be rounded.
     */
    fun fromBigDecimalWithScale(value: BigDecimal, currency: String, scale: Int = value.scale()) = Amount(
      value = value.setScale(scale).unscaledValue().toLong(),
      precision = scale,
    )
  }
}
