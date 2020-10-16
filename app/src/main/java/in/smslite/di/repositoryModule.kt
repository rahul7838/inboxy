package `in`.smslite.di

import `in`.smslite.repository.MessageRepository
import org.koin.dsl.module

val repositoryModule = module {

    single { MessageRepository(get()) }
}