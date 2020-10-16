package `in`.smslite.di

import `in`.smslite.db.MessageDatabase
import org.koin.android.ext.koin.androidContext
import org.koin.dsl.module

val databaseModule = module {

    single { MessageDatabase.getInMemoryDatabase(androidContext()) }
}