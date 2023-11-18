package com.emendo.expensestracker.core.data.mapper.base

/**
 * Helper class to transforms a specific input to desired object output, implementing for that
 * all operations required to transform.
 */
interface Mapper<in F, out T> {

  /**
   * Mapping object.
   *
   * @param from Initial object to from mapping.
   * @return An Object containing the results of applying the transformation.
   */
  suspend fun map(from: F): T
}