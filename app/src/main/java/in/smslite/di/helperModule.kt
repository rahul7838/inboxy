package `in`.smslite.di

import `in`.smslite.activity.MainActivity
import `in`.smslite.others.ContextualActionManager
import org.koin.core.qualifier.named
import org.koin.dsl.module

val helperModule = module {

    scope(named<MainActivity>()) {
        scoped<ContextualActionManager> { ContextualActionManager() }
    }
}