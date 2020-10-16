package `in`.smslite.di

import `in`.smslite.viewModel.LocalMessageDbViewModel
import org.koin.android.ext.koin.androidApplication
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.core.component.KoinApiExtension
import org.koin.dsl.module

@OptIn(KoinApiExtension::class)
val viewModelModule = module {

    viewModel { LocalMessageDbViewModel(androidApplication()) }
}