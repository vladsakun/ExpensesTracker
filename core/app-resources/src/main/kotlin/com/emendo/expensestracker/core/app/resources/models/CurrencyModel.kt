package com.emendo.expensestracker.core.app.resources.models

import androidx.compose.runtime.Stable

@Stable
enum class CurrencyModel constructor(
  val id: Int,
  val currencySymbol: String,
  val currencyName: String,
  val currencyNameAndSymbol: String = currencyName,
) {
  ALL(1, "Lek", "ALL"),
  AMD(2, "֏", "AMD"),
  ANG(3, "ƒ", "ANG"),
  ARS(4, "$", "ARS"),
  AWG(5, "ƒ", "AWG"),
  BBD(6, "Bds$", "BBD"),
  BMD(7, "BMD$", "BMD"),
  BOB(8, "Bs.", "BOB"),
  BRL(9, "R$", "BRL"),
  BSD(10, "B$", "BSD"),
  BGN(11, "лв", "BGN"),
  CAD(12, "CA$", "CAD"),
  CDF(13, "FC", "CDF"),
  CHF(14, "Fr", "CHF"),
  CLP(15, "$", "CLP"),
  CNY(16, "¥", "CNY"),
  COP(17, "$", "COP"),
  CRC(18, "₡", "CRC"),
  CZK(19, "Kč", "CZK"),
  DJF(20, "Fdj", "DJF"),
  DKK(21, "kr", "DKK"),
  DOP(22, "RD$", "DOP"),
  EGP(23, "£", "EGP"),
  ERN(24, "Nfk", "ERN"),
  EUR(25, "€", "EUR"),
  FJD(26, "$", "FJD"),
  GBP(27, "£", "GBP"),
  GIP(28, "£", "GIP"),
  GNF(29, "FG", "GNF"),
  GTQ(30, "Q", "GTQ"),
  HKD(31, "$", "HKD"),
  HNL(32, "L", "HNL"),
  HUF(33, "Ft", "HUF"),
  IDR(34, "Rp", "IDR"),
  ILS(35, "₪", "ILS"),
  INR(36, "₹", "INR"),
  JPY(37, "¥", "JPY"),
  KRW(38, "₩", "KRW"),
  KWD(39, "د.ك", "KWD"),
  KYD(40, "$", "KYD"),
  KHR(41, "៛", "KHR"),
  LTL(42, "Lt", "LTL"),
  MOP(43, "MOP$", "MOP"),
  MUR(44, "₨", "MUR"),
  MXN(45, "$", "MXN"),
  MYR(46, "RM", "MYR"),
  NGN(47, "₦", "NGN"),
  NIO(48, "C$", "NIO"),
  NOK(49, "kr", "NOK"),
  NZD(50, "$", "NZD"),
  PEN(51, "S/", "PEN"),
  PHP(52, "₱", "PHP"),
  PLN(53, "zł", "PLN"),
  QAR(54, "﷼", "QAR"),
  RON(55, "lei", "RON"),
  RUB(56, "₽", "RUB"),
  SAR(57, "﷼", "SAR"),
  SCR(58, "₨", "SCR"),
  SEK(59, "kr", "SEK"),
  SGD(60, "$", "SGD"),
  SLL(61, "Le", "SLL"),
  SOS(62, "Sh", "SOS"),
  THB(63, "฿", "THB"),
  TRY(64, "₺", "TRY"),
  TWD(65, "NT$", "TWD"),
  TTD(66, "TT$", "TTD"),
  UAH(67, "₴", "UAH"),
  USD(68, "$", "USD"),
  VES(69, "Bs", "VES"),
  VND(70, "₫", "VND"),
  XAF(71, "CFA", "XAF"),
  XAG(72, "Ag", "XAG"),
  XAU(73, "Au", "XAU"),
  XCD(74, "$", "XCD"),
  XDR(75, "XDR", "XDR"),
  XOF(76, "CFA", "XOF"),
  XPD(77, "Pd", "XPD"),
  XPF(78, "₣", "XPF"),
  XPT(79, "Pt", "XPT"),
  ZAR(80, "R", "ZAR");

  companion object {
    private val values = entries.associateBy { it.id }

    // Todo remove hardcode
    val DEFAULT = USD

    fun getById(id: Int): CurrencyModel {
      return values[id] ?: throw IllegalArgumentException("No Currency with id $id")
    }
  }
}