package `in`.smslite.adapter

import `in`.smslite.fragments.CategorizeFragment
import `in`.smslite.fragments.ContactsPermissionFragment
import `in`.smslite.fragments.SMSPermissionFragment
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter

class WelcomePageAdapter(fragmentActivity: FragmentActivity, private val countOfPage: Int) : FragmentStateAdapter(fragmentActivity) {

    override fun getItemCount(): Int {
        return countOfPage
    }

    override fun createFragment(position: Int): Fragment {
        return when (position) {
            0 -> SMSPermissionFragment.newInstance()
            1 -> ContactsPermissionFragment.newInstance()
            2 -> CategorizeFragment.newInstance()
            else -> throw Exception("Fragment not found")
        }
    }

}