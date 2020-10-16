package `in`.smslite.activity

import `in`.smslite.R
import `in`.smslite.adapter.SMSAdapter
import `in`.smslite.contacts.Contact
import `in`.smslite.db.Message
import `in`.smslite.others.ContextualActionManager
import `in`.smslite.viewModel.ArchiveMsgViewModel
import android.os.Bundle
import android.preference.PreferenceManager
import android.view.View
import android.widget.ImageView
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.floatingactionbutton.FloatingActionButton
import org.koin.android.ext.android.inject
import org.koin.androidx.scope.lifecycleScope

/**
 * Created by rahul1993 on 6/1/2018.
 */
class ArchiveMessageActivity : AppCompatActivity() {
    private var fragmentBottomNavigationView: BottomNavigationView? = null
    private var fragmentFab: FloatingActionButton? = null
    private var fragmentRecyclerView: RecyclerView? = null
    private var fragmentToolbar: Toolbar? = null
    private var smsAdapter: SMSAdapter? = null
    private var emptyImage: ImageView? = null
    private var emptyText: TextView? = null
    private var emptyView: RelativeLayout? = null

    private val archiveMsgViewModel: ArchiveMsgViewModel by inject()
    private val contextualActionManager: ContextualActionManager by lifecycleScope.inject()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        PreferenceManager.getDefaultSharedPreferences(this).edit().putInt(getString(R.string.dialog_option), Contact.ARCHIVE).apply()
        setContentView(R.layout.activity_main)
        fragmentToolbar = findViewById(R.id.toolbar)
        fragmentFab = findViewById(R.id.fab)
        fragmentBottomNavigationView = findViewById(R.id.bottom_navigation)
        fragmentRecyclerView = findViewById(R.id.sms_list)
        emptyImage = findViewById(R.id.empty_image_view)
        emptyText = findViewById(R.id.empty_text_view)
        emptyView = findViewById(R.id.empty_main_view)
        setToolbar()
        setLinearLayout()
        smsAdapter = SMSAdapter(listOf(), listOf())
        fragmentRecyclerView?.adapter = smsAdapter
        subscribeUI()
    }

    private fun setLinearLayout() {
        val linearLayoutManager = LinearLayoutManager(this)
        fragmentRecyclerView?.layoutManager = linearLayoutManager
        fragmentRecyclerView?.setHasFixedSize(true)
    }

    private fun setToolbar() {
        setSupportActionBar(fragmentToolbar)
        title = "Archive"
        val bar = supportActionBar
        bar?.setDisplayHomeAsUpEnabled(true)
    }

    private fun subscribeUI() {
        archiveMsgViewModel.archiveMessage.observe(this, { messages: List<Message> -> setMessage(messages) })
    }

    private fun setMessage(messages: List<Message>) {
        if (messages.isEmpty()) {
            fragmentRecyclerView?.visibility = View.GONE
            emptyView?.visibility = View.VISIBLE
            emptyImage?.setImageDrawable(getDrawable(R.drawable.ic_archive_black_24dp))
            emptyText?.text = "You do not have any archived message"
        } else {
            smsAdapter?.setMessage(messages)
            fragmentRecyclerView?.visibility = View.VISIBLE
            emptyView?.visibility = View.GONE
            contextualActionManager.contextualActionMode(fragmentRecyclerView!!, fragmentFab!!, fragmentBottomNavigationView!!, smsAdapter,
                    this, this, "archive", messages)
        }
    }

    override fun onBackPressed() {
        super.onBackPressed()
        finish()
    }
}