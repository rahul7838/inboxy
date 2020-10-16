package `in`.smslite.di

import `in`.smslite.db.MessageDatabase
import `in`.smslite.repository.MessageRepository
import org.koin.android.ext.koin.androidContext
import org.koin.dsl.module

val repositoryModule = module {

    single { MessageRepository(get()) }

    single { MessageDatabase.getInMemoryDatabase(androidContext())?.messageDao() }
}