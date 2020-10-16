package `in`.smslite.di

import `in`.smslite.fragments.CategorizeFragment
import `in`.smslite.fragments.ContactsPermissionFragment
import `in`.smslite.fragments.PermissionFragment
import `in`.smslite.fragments.SMSPermissionFragment
import org.koin.androidx.fragment.dsl.fragment
import org.koin.dsl.module

val fragmentModule = module {

    fragment { PermissionFragment() }
    fragment { SMSPermissionFragment() }
    fragment { ContactsPermissionFragment() }
    fragment { CategorizeFragment() }
}