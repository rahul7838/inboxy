package `in`.smslite

import android.app.Application
import android.telephony.TelephonyManager
import com.facebook.stetho.Stetho
import org.koin.android.ext.koin.androidContext
import org.koin.androidx.fragment.koin.fragmentFactory
import org.koin.core.context.startKoin
import timber.log.Timber

/**
 * Created by rahul1993 on 11/11/2017.
 */
class SMSApplication : Application() {
    private var mCountryIso: String? = null
    override fun onCreate() {
        super.onCreate()
        //    Fabric.with(this, new Crashlytics());
        if (!BuildConfig.DEBUG) {
//      Fabric.with(this, new Answers());
        }
        Timber.plant(Timber.DebugTree())
        application = this
        //    if (LeakCanary.isInAnalyzerProcess(this)) {
        // This process is dedicated to LeakCanary for heap analysis.
        // You should not init your app in this process.
//      return;
//    }
//    LeakCanary.install(this);
        // Normal app init code...
        Stetho.initializeWithDefaults(this)
        startKoin {
            fragmentFactory()
            androidContext(this@SMSApplication)
            modules(modules = `in`.smslite.di.modules)
        }
    }

    val currentCountryIso: String?
        get() {
            if (mCountryIso == null) {
                val tm = this.getSystemService(TELEPHONY_SERVICE) as TelephonyManager
                mCountryIso = tm.networkCountryIso
            }
            return mCountryIso
        }

    companion object {
        @get:Synchronized
        var application: SMSApplication? = null
            private set
    }
}