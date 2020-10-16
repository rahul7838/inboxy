package `in`.smslite.activity

import `in`.smslite.R
import `in`.smslite.adapter.SMSAdapter
import `in`.smslite.contacts.Contact
import `in`.smslite.db.Message
import `in`.smslite.others.ContextualActionManager
import `in`.smslite.viewModel.BlockedMessageActivityViewModel
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

/**
 * Created by rahul1993 on 5/31/2018.
 */
class BlockedMessageActivity : AppCompatActivity() {
    var fragmentBottomNavigationView: BottomNavigationView? = null
    var fragmentFab: FloatingActionButton? = null
    var fragmentRecyclerView: RecyclerView? = null
    private var fragmentToolbar: Toolbar? = null
    private var smsAdapter: SMSAdapter? = null
    private var emptyImage: ImageView? = null
    private var emptyText: TextView? = null
    private var emptyView: RelativeLayout? = null

    private val blockedMessageActivityViewModel: BlockedMessageActivityViewModel by inject()
    private val contextualActionManager: ContextualActionManager by inject()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        PreferenceManager.getDefaultSharedPreferences(this).edit().putInt(getString(R.string.dialog_option), Contact.BLOCKED).apply()
        setContentView(R.layout.activity_main)
        fragmentToolbar = findViewById(R.id.toolbar)
        fragmentFab = findViewById(R.id.fab)
        fragmentBottomNavigationView = findViewById(R.id.bottom_navigation)
        fragmentRecyclerView = findViewById(R.id.sms_list)
        emptyImage = findViewById(R.id.empty_image_view)
        emptyText = findViewById(R.id.empty_text_view)
        emptyView = findViewById(R.id.empty_main_view)
        fragmentBottomNavigationView?.visibility = View.GONE
        //    fragmentFab.setVisibility(View.GONE);
        setToolbar()
        setLinearLayout()
        smsAdapter = SMSAdapter(listOf(), listOf())
        fragmentRecyclerView?.adapter = smsAdapter
        val mainActivityHelper = ContextualActionManager()
        subscribeUI()
    }

    private fun setLinearLayout() {
        val linearLayoutManager = LinearLayoutManager(this)
        fragmentRecyclerView!!.layoutManager = linearLayoutManager
        fragmentRecyclerView!!.setHasFixedSize(true)
    }

    private fun setToolbar() {
        setSupportActionBar(fragmentToolbar)
        title = "Blocked"
        val bar = supportActionBar
        bar?.setDisplayHomeAsUpEnabled(true)
    }

    private fun subscribeUI() {
        blockedMessageActivityViewModel.blockedMessage.observe(this, { messages: List<Message> -> setMessage(messages) })
    }

    private fun setMessage(messages: List<Message>) {
        if (messages.isEmpty()) {
            fragmentRecyclerView!!.visibility = View.GONE
            emptyView?.visibility = View.VISIBLE
            emptyImage?.setImageDrawable(getDrawable(R.drawable.ic_block_black_24dp))
            emptyText?.text = "No sender is Blocked"
        } else {
            smsAdapter?.setMessage(messages)
            fragmentRecyclerView?.visibility = View.VISIBLE
            emptyView?.visibility = View.GONE
            contextualActionManager.contextualActionMode(fragmentRecyclerView!!, fragmentFab!!, fragmentBottomNavigationView!!, smsAdapter,
                    this, this, "blocked", messages)
        }
    }
}