package com.emendo.expensestracker.di

import dagger.hilt.DefineComponent
import dagger.hilt.components.SingletonComponent

@DefineComponent(parent = SingletonComponent::class)
interface ExpensesTrackerAppComponent

