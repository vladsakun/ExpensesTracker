package com.emendo.expensestracker.categories.common

import com.emendo.expensestracker.core.model.data.command.CastCommand

interface CategoryCastCommand<T> : CastCommand<T, CategoryCommandReceiver> where T : CategoryCommandReceiver