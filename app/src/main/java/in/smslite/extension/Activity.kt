package `in`.smslite.extension

import `in`.smslite.R
import android.app.Activity
import android.content.Intent
import android.os.Bundle
import androidx.annotation.IdRes
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.navigation.ActivityNavigator
import androidx.navigation.NavOptions

inline fun <reified T> Activity.navigateTo(
        bundle: Bundle? = null,
        navOptions: NavOptions? = enterRight(),
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

fun Activity.enterRight(): NavOptions {
    return NavOptions.Builder()
            .setEnterAnim(R.anim.enter_right)
            .setExitAnim(R.anim.exit_right)
            .setPopExitAnim(R.anim.exit_right)
            .setPopEnterAnim(R.anim.exit_right)
            .build()
}

fun FragmentActivity.addFragment(
        @IdRes containerViewId: Int,
        fragment: Fragment,
        addToBackStack: Boolean = false
) {
    supportFragmentManager.beginTransaction().run {
        this.add(containerViewId, fragment, fragment::class.java.simpleName)
        if (addToBackStack) addToBackStack(fragment::class.java.simpleName)
        supportFragmentManager.executePendingTransactions()
        commitNow()
    }
}

inline fun <reified T> FragmentActivity.findFragment(): T? {
    return supportFragmentManager.findFragmentByTag(T::class.java.simpleName) as? T

}