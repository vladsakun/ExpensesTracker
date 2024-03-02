package com.emendo.expensestracker.core.model.data.command

/**
 * A command that can be executed on a receiver.
 *
 * @param R - receiver type
 */
interface Command<R> {
  fun execute(receiver: R)
}

/**
 * A command that can be executed on a receiver.
 *
 * @param T - concrete receiver type to be casted to
 * @param R - base receiver type
 */
interface CastCommand<T, R> : Command<R> where T : R {
  fun executeCast(receiver: T)

  override fun execute(receiver: R) {
    executeCast(receiver as T)
  }
}