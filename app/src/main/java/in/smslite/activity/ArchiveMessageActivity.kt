package `in`.smslite.activity

import `in`.smslite.R
import `in`.smslite.adapter.SMSAdapter
import `in`.smslite.contacts.Contact
import `in`.smslite.db.Message
import `in`.smslite.others.MainActivityHelper
import `in`.smslite.viewModel.ArchiveMsgViewModel
import android.app.Activity
import android.content.Context
import android.os.Bundle
import android.preference.PreferenceManager
import android.view.View
import android.widget.ImageView
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.floatingactionbutton.FloatingActionButton
import java.util.*

/**
 * Created by rahul1993 on 6/1/2018.
 */
class ArchiveMessageActivity : AppCompatActivity() {
    private var fragmentBottomNavigationView: BottomNavigationView? = null
    private var fragmentFab: FloatingActionButton? = null
    private var fragmentRecyclerView: RecyclerView? = null
    private var fragmentToolbar: Toolbar? = null
    private val selectedItem: List<Message> = ArrayList()
    private val listOfItem: MutableList<Message> = ArrayList()
    private var activity: Activity? = null
    private var context: Context? = null
    private var archiveMessageViewModel: ArchiveMsgViewModel? = null
    private var liveMsgList: LiveData<List<Message>>? = null
    private var smsAdapter: SMSAdapter? = null
    private var emptyImage: ImageView? = null
    private var emptyText: TextView? = null
    private var emptyView: RelativeLayout? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        activity = this
        context = this
        PreferenceManager.getDefaultSharedPreferences(context).edit().putInt(getString(R.string.dialog_option), Contact.ARCHIVE).apply()
        setContentView(R.layout.activity_main)
        fragmentToolbar = findViewById(R.id.toolbar)
        fragmentFab = findViewById(R.id.fab)
        fragmentBottomNavigationView = findViewById(R.id.bottom_navigation)
        fragmentRecyclerView = findViewById(R.id.sms_list)
        emptyImage = findViewById(R.id.empty_image_view)
        emptyText = findViewById(R.id.empty_text_view)
        emptyView = findViewById(R.id.empty_main_view)
//        fragmentBottomNavigationView.setVisibility(View.GONE)
//        fragmentFab.setVisibility(View.GONE)
        setToolbar()
        setLinearLayout()
        archiveMessageViewModel = ViewModelProviders.of(this).get<ArchiveMsgViewModel>(ArchiveMsgViewModel::class.java)
        val list: List<Message> = ArrayList()
        smsAdapter = SMSAdapter(list, selectedItem, listOfItem)
        fragmentRecyclerView?.adapter = smsAdapter
        val mainActivityHelper = MainActivityHelper()
        mainActivityHelper.contextualActionMode(fragmentRecyclerView!!, fragmentFab!!, fragmentBottomNavigationView!!, smsAdapter,
                this, this, "archive", listOfItem)
        subscribeUI()
    }

    private fun setLinearLayout() {
        val linearLayoutManager = LinearLayoutManager(this)
        fragmentRecyclerView!!.layoutManager = linearLayoutManager
        fragmentRecyclerView!!.setHasFixedSize(true)
    }

    private fun setToolbar() {
        setSupportActionBar(fragmentToolbar)
        title = "Archive"
        val bar = supportActionBar
        bar?.setDisplayHomeAsUpEnabled(true)
    }

    private fun subscribeUI() {
        liveMsgList = archiveMessageViewModel?.archiveMessage
        liveMsgList!!.observe(this, { messages: List<Message> -> setMessage(messages) })
    }

    private fun setMessage(messages: List<Message>) {
        if (messages.isEmpty()) {
            fragmentRecyclerView!!.visibility = View.GONE
            emptyView?.visibility = View.VISIBLE
            emptyImage?.setImageDrawable(getDrawable(R.drawable.ic_archive_black_24dp))
            emptyText?.text = "You do not have any archived message"
        } else {
            listOfItem.clear() // if dont do this on 2nd long press if u try to move message it will not move
            listOfItem.addAll(messages)
            smsAdapter?.setMessage(messages)
            fragmentRecyclerView!!.visibility = View.VISIBLE
            emptyView?.visibility = View.GONE
        }
    }

    override fun onBackPressed() {
        super.onBackPressed()
        activity!!.finish()
    }

    companion object {
        private val TAG = ArchiveMessageActivity::class.java.simpleName
    }
}