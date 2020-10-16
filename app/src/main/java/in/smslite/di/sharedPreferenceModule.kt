package `in`.smslite.di

import androidx.security.crypto.EncryptedSharedPreferences
import androidx.security.crypto.MasterKey
import org.koin.android.ext.koin.androidContext
import org.koin.dsl.module

const val INBOXY_PREFERENCE = "inboxyPreference"
val sharedPreferenceModule = module {
    single {
        val masterKey = MasterKey.Builder(androidContext())
                .setKeyScheme(MasterKey.KeyScheme.AES256_GCM)
                .build()
        EncryptedSharedPreferences.create(
                androidContext(),
                INBOXY_PREFERENCE,
                masterKey,
                EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
                EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
        )
    }
}