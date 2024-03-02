package com.emendo.expensestracker.categories.common.command

import com.emendo.expensestracker.core.model.data.command.CastCommand

interface CategoryCastCommand<T> : CastCommand<T, CategoryCommandReceiver> where T : CategoryCommandReceiver