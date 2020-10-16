package `in`.smslite.extension

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import androidx.navigation.ActivityNavigator
import androidx.navigation.NavOptions

inline fun <reified T> Activity.navigateTo(
        bundle: Bundle? = null,
        navOptions: NavOptions? = null,
        navExtras: ActivityNavigator.Extras? = null
) {
    ActivityNavigator(this).run {
        navigate(
                createDestination().setIntent(Intent(this@navigateTo, T::class.java)),
                bundle,
                navOptions,
                navExtras
        )
    }
}