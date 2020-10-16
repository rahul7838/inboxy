package `in`.smslite.di

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import org.koin.dsl.module

val coroutineModule = module {

    factory { CoroutineScope(Dispatchers.IO) }

    factory { CoroutineScope(Dispatchers.Default) }

}