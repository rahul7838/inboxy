package `in`.smslite.activity

import `in`.smslite.BottomNavigationViewHelper
import `in`.smslite.R
import `in`.smslite.adapter.SMSAdapter
import `in`.smslite.contacts.Contact
import `in`.smslite.contacts.PhoneContact
import `in`.smslite.databinding.ActivityMainBinding
import `in`.smslite.db.Message
import `in`.smslite.db.MessageDatabase
import `in`.smslite.others.MainActivityHelper
import `in`.smslite.threads.UpdateSentMsgThread
import `in`.smslite.utils.AppStartUtils
import `in`.smslite.utils.MessageUtils
import `in`.smslite.utils.NotificationUtils
import `in`.smslite.utils.ThreadUtils
import `in`.smslite.viewModel.LocalMessageDbViewModel
import android.Manifest
import android.content.Intent
import android.content.SharedPreferences
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.preference.PreferenceManager
import android.provider.Telephony
import android.text.Html
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.lifecycle.LiveData
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import org.koin.android.ext.android.inject

class MainActivity : AppCompatActivity() {
    private val selectedItem: List<Message> = ArrayList()
    private val listOfItem: MutableList<Message> = ArrayList()
    private var currentVisiblePostion = 0
    var liveDataListMsg: LiveData<List<Message>>? = null
    var observer: Observer<List<Message>>? = null
    var llm: LinearLayoutManager? = null
    var smsAdapter: SMSAdapter? = null
    var permissionNeeded: ArrayList<String>? = null
    var sharedPreferences: SharedPreferences? = null
    private var category = 0
    private val OTPTestThreadStarted = false
    private var binding: ActivityMainBinding? = null
    private val localMessageDbViewModel: LocalMessageDbViewModel by inject()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // setDefaultValue should be defined at a place from where app can enter first.
        PreferenceManager.setDefaultValues(this, R.xml.preferences, false)
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this)
        //    Fabric.with(this, new Crashlytics());---- setup crashlytics for debug and release build
        Log.d(TAG, "onCreate")
        ThreadUtils.cachePrimaryContactName().start()
        //    registerReceiverForSmsBroadCast();
        db = MessageDatabase.getInMemoryDatabase(this)
        val smsCategorized = sharedPreferences?.getBoolean(getString(R.string.key_sms_categorized), false)
        when (AppStartUtils.checkAppStart(this, sharedPreferences)) {
            else -> checkPermission(smsCategorized == true)
        }
    }

    private fun checkPermission(smsCategorized: Boolean) {
        Log.i(TAG, "checkPermission")
        permissionNeeded = ArrayList()
        val permissionCheckSms = ContextCompat.checkSelfPermission(this@MainActivity, Manifest.permission.READ_SMS)
        val permissionCheckContact = ContextCompat.checkSelfPermission(this@MainActivity, Manifest.permission.READ_CONTACTS)
        val permissionCheckPhoneState = ContextCompat.checkSelfPermission(this@MainActivity, Manifest.permission.READ_PHONE_STATE)
        if (permissionCheckSms != PackageManager.PERMISSION_GRANTED) {
            permissionNeeded?.add(Manifest.permission.READ_SMS)
        }
        if (permissionCheckContact != PackageManager.PERMISSION_GRANTED) {
            permissionNeeded?.add(Manifest.permission.READ_CONTACTS)
        }
        if (permissionCheckPhoneState != PackageManager.PERMISSION_GRANTED) {
            permissionNeeded?.add(Manifest.permission.READ_PHONE_STATE)
        }
        if (permissionNeeded?.isEmpty() != true || !smsCategorized) {
//    ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_SMS, Manifest.permission.READ_CONTACTS}, MY_PERMISSIONS_REQUEST_READ_SMS);
            val intent = Intent(this, WelcomeActivity::class.java)
            startActivity(intent)
            finish()
        } else {
            initiUi()
        }
    }

    override fun onPause() {
        super.onPause()
        if (llm != null) {
            currentVisiblePostion = llm!!.findLastCompletelyVisibleItemPosition()
        }
    }

    override fun onResume() {
        super.onResume()
        Log.d(TAG, "onResume")
        if (llm != null) {
            llm!!.scrollToPosition(currentVisiblePostion)
        }
        sharedPreferences!!.edit().putInt(getString(R.string.dialog_option), category).apply()
        if (!MessageUtils.checkIfDefaultSms(this)) {
            UpdateSentMsgThread(this).start()
        }


//
    }

    private fun initiUi() {
//    updateWidgetColumn();
        Log.d(TAG, "initiUi")
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding!!.root)
        setLinearLayout()
        setToolbar()
        PhoneContact.initialize(this)

//    if Mainactivity is open through pending intent, the below code ensure which sms category to display.
        if (intent.extras != null && intent.extras!!.getInt(NotificationUtils.BROADCAST_SMS_CATEGORY_KEY) != 0) {
            val bundle = intent.extras
            val broadcastSmsCategory = bundle!!.getInt(NotificationUtils.BROADCAST_SMS_CATEGORY_KEY)
            Thread {
                localMessageDbViewModel?.markAllSeen(broadcastSmsCategory)
                Log.i("MainActivity", "markAllseenDone")
            }.start()
            subscribeUi(broadcastSmsCategory)
            setItemMenuChecked(broadcastSmsCategory)
            Log.d(TAG, "BRoadcast")
        } else {
            subscribeUi(Contact.PRIMARY)
            Log.d(TAG, "addParentStack")
        }
        setBottomNavigation()
        //    clickListener();
    }

    private fun setLinearLayout() {
        llm = LinearLayoutManager(this)
        llm!!.orientation = LinearLayoutManager.VERTICAL
        binding!!.appBarMain.contentMain.smsList.layoutManager = llm
        binding!!.appBarMain.contentMain.smsList.setHasFixedSize(true)
        val list: List<Message> = ArrayList()
        smsAdapter = SMSAdapter(list, selectedItem, listOfItem)
        binding!!.appBarMain.contentMain.smsList.adapter = smsAdapter
        val mainActivityHelper = MainActivityHelper()
        mainActivityHelper.contextualActionMode(binding!!.appBarMain.contentMain.smsList, binding!!.appBarMain.contentMain.fab, binding!!.appBarMain.contentMain.bottomNavigation, smsAdapter, this, this, "mainActivity", listOfItem)
    }

    private fun setToolbar() {
        binding!!.appBarMain.toolbar.setTitle(R.string.title_primary)
        setSupportActionBar(binding!!.appBarMain.toolbar)
    }

    fun subscribeUi(category: Int) {
        this.category = category
        localMessageDbViewModel.getMessageListByCategory(category).observe(this, { setMessageList(it, category) })
    }

    fun clickFab(view: View?) {
        if (!MessageUtils.checkIfDefaultSms(this)) {
            val intent = Intent(Telephony.Sms.Intents.ACTION_CHANGE_DEFAULT)
            intent.putExtra(Telephony.Sms.Intents.EXTRA_PACKAGE_NAME, this!!.packageName)
            startActivityForResult(intent, 8)
        } else {
            launchPickContact()
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == 8) {
            if (resultCode == RESULT_OK) {
                launchPickContact()
            }
        }
        if (requestCode == PICK_CONTACT_REQUEST_CODE) {
            // Make sure the request was successful
            if (resultCode == RESULT_OK) {
                launchCompleteSmsActivity(data)
                val error: Throwable = Error("msg")
                Log.v(TAG, "log", error)
            }
        }
    }

    private fun launchPickContact() {
        val intent = Intent(this, SelectContactActivity::class.java)
        startActivity(intent)
    }

    fun launchCompleteSmsActivity(data: Intent?) {
        val number: String = data?.let { localMessageDbViewModel?.pickContactSelected(it) } ?: ""
        val intent = Intent(this, CompleteSmsActivity::class.java)
        intent.putExtra(this!!.getString(R.string.address_id), number)
        startActivity(intent)
    }

    private fun setBottomNavigation() {
//    bottomNavigationView = (BottomNavigationView) findViewById(R.id.bottom_navigation);
        BottomNavigationViewHelper.disableShiftMode(binding!!.appBarMain.contentMain.bottomNavigation)
        binding!!.appBarMain.contentMain.bottomNavigation.setOnNavigationItemSelectedListener { item: MenuItem ->
            val id = item.itemId
            if (id == R.id.bottomNav_primary) {
                sharedPreferences!!.edit().putInt(getString(R.string.dialog_option), Contact.PRIMARY).apply()
                subscribeUi(Contact.PRIMARY)
                binding!!.appBarMain.toolbar.setTitle(R.string.title_primary)
            } else if (id == R.id.bottomNav_finance) {
                sharedPreferences!!.edit().putInt(getString(R.string.dialog_option), Contact.FINANCE).apply()
                subscribeUi(Contact.FINANCE)
                binding!!.appBarMain.toolbar.setTitle(R.string.title_finance)
            } else if (id == R.id.bottomNav_promotion) {
                sharedPreferences!!.edit().putInt(getString(R.string.dialog_option), Contact.PROMOTIONS).apply()
                subscribeUi(Contact.PROMOTIONS)
                binding!!.appBarMain.toolbar.setTitle(R.string.title_promotions)
            } else if (id == R.id.bottomNav_updates) {
                sharedPreferences!!.edit().putInt(getString(R.string.dialog_option), Contact.UPDATES).apply()
                subscribeUi(Contact.UPDATES)
                binding!!.appBarMain.toolbar.setTitle(R.string.title_updates)
            }
            true
        }
    }

    private fun setMessageList(messageList: List<Message>, category: Int) {
        listOfItem.clear()
        listOfItem.addAll(messageList)
        if (messageList.isEmpty()) {
            when (category) {
                Contact.PRIMARY -> {
                    binding!!.appBarMain.contentMain.emptyMain.emptyTextView.setText(R.string.empty_primary_text)
                    binding!!.appBarMain.contentMain.emptyMain.emptyImageView.setImageDrawable(resources.getDrawable(R.drawable.ic_menu_primary, null))
                }
                Contact.FINANCE -> {
                    binding!!.appBarMain.contentMain.emptyMain.emptyTextView.setText(R.string.empty_finance_text)
                    binding!!.appBarMain.contentMain.emptyMain.emptyImageView.setImageDrawable(resources.getDrawable(R.drawable.ic_menu_finance, null))
                }
                Contact.PROMOTIONS -> {
                    binding!!.appBarMain.contentMain.emptyMain.emptyTextView.setText(R.string.empty_promotion_text)
                    binding!!.appBarMain.contentMain.emptyMain.emptyImageView.setImageDrawable(resources.getDrawable(R.drawable.ic_menu_promotions, null))
                }
                Contact.UPDATES -> {
                    binding!!.appBarMain.contentMain.emptyMain.emptyTextView.setText(R.string.empty_update_text)
                    binding!!.appBarMain.contentMain.emptyMain.emptyImageView.setImageDrawable(resources.getDrawable(R.drawable.ic_menu_updates, null))
                }
            }
            binding!!.appBarMain.contentMain.smsList.visibility = View.GONE
            binding!!.appBarMain.contentMain.emptyMain.emptyMainView.visibility = View.VISIBLE
        } else {
            binding!!.appBarMain.contentMain.emptyMain.emptyMainView.visibility = View.GONE
            binding!!.appBarMain.contentMain.smsList.visibility = View.VISIBLE
            smsAdapter?.setMessage(messageList)
        }
    }

    fun setItemMenuChecked(category: Int) {
        sharedPreferences!!.edit().putInt(getString(R.string.dialog_option), category).apply()
        BottomNavigationViewHelper.disableShiftMode(binding!!.appBarMain.contentMain.bottomNavigation)
        when (category) {
            1 -> {
                binding!!.appBarMain.contentMain.bottomNavigation.menu.getItem(0).isChecked = true
                binding!!.appBarMain.toolbar.setTitle(R.string.title_primary)
            }
            2 -> {
                binding!!.appBarMain.contentMain.bottomNavigation.menu.getItem(1).isChecked = true
                binding!!.appBarMain.toolbar.setTitle(R.string.title_finance)
            }
            3 -> {
                binding!!.appBarMain.contentMain.bottomNavigation.menu.getItem(2).isChecked = true
                binding!!.appBarMain.toolbar.setTitle(R.string.title_promotions)
            }
            4 -> {
                binding!!.appBarMain.contentMain.bottomNavigation.menu.getItem(3).isChecked = true
                binding!!.appBarMain.toolbar.setTitle(R.string.title_updates)
            }
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        super.onOptionsItemSelected(item)
        val id = item.itemId
        if (id == R.id.menu_share) {
            val shareIntent = Intent(Intent.ACTION_SEND)
            shareIntent.type = "text/plain"
            var message = getString(R.string.share_message)
            message = message + Html.fromHtml(getString(R.string.playstore_link))
            shareIntent.putExtra(Intent.EXTRA_TEXT, message)
            startActivity(Intent.createChooser(shareIntent, getString(R.string.send_to)))
            //      Answers.getInstance().logShare(new ShareEvent());
        } else if (id == R.id.menu_rate_us) {
            val rateIntent = Intent(Intent.ACTION_VIEW,
                    Uri.parse(getString(R.string.playstore_link)))
            startActivity(rateIntent)
        } else if (id == R.id.menu_settings) {
            val intent = Intent(this, SettingsActivity::class.java)
            startActivity(intent)
            //      Answers.getInstance().logCustom(new CustomEvent("Setting viewed"));
        } else if (id == R.id.menu_search_msg_id) {
            val intent = Intent(this, SearchActivity::class.java)
            startActivity(intent)
        } else if (id == R.id.menu_blocked) {
            val intent = Intent(this, BlockedMessageActivity::class.java)
            startActivity(intent)
        } else if (id == R.id.menu_archive) {
            val intent = Intent(this, ArchiveMessageActivity::class.java)
            startActivity(intent)
        } else if (id == R.id.menu_mark_all_read) {
//      new ThreadUtils.MarkAllReadThread().start();
//      ContentProviderUtil.markAllRead(context);
        }
        return true
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        super.onCreateOptionsMenu(menu)
        menuInflater.inflate(R.menu.menu_main, menu)
        return true
    }

    companion object {
        private val TAG = MainActivity::class.java.simpleName

        @JvmField
        var db: MessageDatabase? = null
        private const val PICK_CONTACT_REQUEST_CODE = 12
        const val WIDGET_UPDATE_DB_COLUMN_KEY = "updateWidgetDb"
        const val MAINACTIVTY_CATEGORY_TASKSTACK_KEY = "category"

        @JvmField
        var localMessageDbViewModel: LocalMessageDbViewModel? = null
    }
}