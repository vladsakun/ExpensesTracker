package com.emendo.expensestracker.core.app.common.ext

fun <T> List<T>.getNextItem(index: Int): T {
  if (isEmpty()) {
    throw NoSuchElementException("List is empty")
  }

  val normalizedIndex = (index + 1) % size
  return get(normalizedIndex)
}