package `in`.smslite.activity

import `in`.smslite.adapter.WelcomePageAdapter
import `in`.smslite.databinding.ActivityWelcomeBinding
import `in`.smslite.fragments.PermissionFragment
import android.annotation.SuppressLint
import android.content.SharedPreferences
import android.os.Bundle
import android.view.GestureDetector
import android.view.MotionEvent
import com.google.android.material.tabs.TabLayoutMediator
import org.koin.android.ext.android.inject
import org.koin.androidx.fragment.android.setupKoinFragmentFactory
import timber.log.Timber

private const val NUM_PAGES: Int = 3

class WelcomeActivity : BaseActivity() {

    private lateinit var binding: ActivityWelcomeBinding
    private val sharedPreferences: SharedPreferences by inject()
    private val permissionFragment: PermissionFragment by inject()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setupKoinFragmentFactory()
        binding = ActivityWelcomeBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setViewPager()
    }

    private fun setViewPager() {
        binding.viewPager2.adapter = WelcomePageAdapter(this, NUM_PAGES)
        binding.run {
            TabLayoutMediator(tabLayout, viewPager2) { _, _ -> }.attach()
        }
        viewPagerListener()
    }

    @SuppressLint("ClickableViewAccessibility")
    private fun viewPagerListener() {
        val detector = GestureDetector(this, Gesture())
        binding.viewPager2.getChildAt(0).setOnTouchListener { v, e ->
            if (detector.onTouchEvent(e)) {
                true
            } else {
                super.onTouchEvent(e)
            }
        }
    }

    inner class Gesture : GestureDetector.SimpleOnGestureListener() {
        override fun onDown(e: MotionEvent?): Boolean {
            return true
        }

        override fun onScroll(e1: MotionEvent?, e2: MotionEvent?, distanceX: Float, distanceY: Float): Boolean {
            Timber.d("MOtionEvent1 $e1")
            Timber.d("MOtionEvent2 $e2")
            return if (e1 != null && e2 != null) {
                if (e1.x > e2.x) {
                    checkPolicy()
                } else {
                    false
                }
            } else {
                false
            }
        }
    }

    private fun checkPolicy(): Boolean {
        val fragment = supportFragmentManager.findFragmentByTag("f${binding.viewPager2.currentItem}")
        return if (fragment is SlidingPolicy) {
            if (fragment.isPolicyRespected()) {
                binding.viewPager2.isUserInputEnabled = true
                false
            } else {
                binding.viewPager2.isUserInputEnabled = false
                fragment.onUserIllegallyRequestedNextPage()
                false
            }
        } else {
            true
        }
    }

    interface SlidingPolicy {
        fun isPolicyRespected(): Boolean

        fun onUserIllegallyRequestedNextPage()
    }
}