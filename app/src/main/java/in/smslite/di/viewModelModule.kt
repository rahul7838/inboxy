package `in`.smslite.di

import `in`.smslite.viewModel.*
import org.koin.android.ext.koin.androidApplication
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

val viewModelModule = module {

    viewModel { LocalMessageDbViewModel(androidApplication()) }

    viewModel { SearchViewModel(androidApplication()) }

    viewModel { ArchiveMsgViewModel(androidApplication()) }

    viewModel { BlockedMessageActivityViewModel(androidApplication()) }

    viewModel { CompleteSmsActivityViewModel(androidApplication()) }
}