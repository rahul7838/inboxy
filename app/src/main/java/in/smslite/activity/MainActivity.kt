package `in`.smslite.activity

import `in`.smslite.R
import `in`.smslite.adapter.SMSAdapter
import `in`.smslite.contacts.Contact
import `in`.smslite.contacts.PhoneContact
import `in`.smslite.databinding.ActivityMainBinding
import `in`.smslite.db.Message
import `in`.smslite.extension.navigateTo
import `in`.smslite.others.ContextualActionManager
import `in`.smslite.threads.UpdateSentMsgThread
import `in`.smslite.utils.MessageUtils
import `in`.smslite.utils.NotificationUtils
import `in`.smslite.viewModel.MainViewModel
import android.Manifest
import android.content.Intent
import android.content.SharedPreferences
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.provider.Telephony
import android.text.Html
import android.view.Menu
import android.view.MenuItem
import android.view.View
import androidx.core.content.ContextCompat
import androidx.preference.PreferenceManager
import androidx.recyclerview.widget.LinearLayoutManager
import org.koin.android.ext.android.inject
import org.koin.androidx.scope.lifecycleScope

class MainActivity : BaseActivity() {

    private var currentVisiblePostion = 0
    var llm: LinearLayoutManager? = null
    private val listOfItem: MutableList<Message> = ArrayList()
    private val selectedItem: List<Message> = ArrayList()

    private val mainViewModel: MainViewModel by inject()
    private val encryptedSharedPreference: SharedPreferences by inject()
    private val contextualActionManager: ContextualActionManager by lifecycleScope.inject()
    private lateinit var binding: ActivityMainBinding
    private lateinit var smsAdapter: SMSAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // setDefaultValue should be defined at a place from where app can enter first.
        PreferenceManager.setDefaultValues(this, R.xml.preferences, false)
        val smsCategorized = encryptedSharedPreference.getBoolean(getString(R.string.key_sms_categorized), false)
        checkPermission(smsCategorized)
    }


    private fun checkPermission(smsCategorized: Boolean) {
        val permissionNeeded = ArrayList<String>()
        val permissionCheckSms = ContextCompat.checkSelfPermission(this, Manifest.permission.READ_SMS)
        val permissionCheckContact = ContextCompat.checkSelfPermission(this, Manifest.permission.READ_CONTACTS)
        val permissionCheckPhoneState = ContextCompat.checkSelfPermission(this, Manifest.permission.READ_PHONE_STATE)
        if (permissionCheckSms != PackageManager.PERMISSION_GRANTED) {
            permissionNeeded.add(Manifest.permission.READ_SMS)
        }
        if (permissionCheckContact != PackageManager.PERMISSION_GRANTED) {
            permissionNeeded.add(Manifest.permission.READ_CONTACTS)
        }
        if (permissionCheckPhoneState != PackageManager.PERMISSION_GRANTED) {
            permissionNeeded.add(Manifest.permission.READ_PHONE_STATE)
        }
        if (permissionNeeded.isNotEmpty() || !smsCategorized) {
            navigateTo<WelcomeActivity>()
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
        if (llm != null) {
            llm!!.scrollToPosition(currentVisiblePostion)
        }
        if (!MessageUtils.checkIfDefaultSms(this)) {
            UpdateSentMsgThread(this).start()
        }
    }

    private fun initiUi() {
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setLinearLayout()
        setToolbar()
        PhoneContact.initialize(this) //TODO need to check

//    if Mainactivity is open through pending intent, the below code ensure which sms category to display.
        if (intent.extras != null && intent.extras?.getInt(NotificationUtils.BROADCAST_SMS_CATEGORY_KEY) != 0) {
            val bundle = intent.extras
            val broadcastSmsCategory = bundle?.getInt(NotificationUtils.BROADCAST_SMS_CATEGORY_KEY)
            mainViewModel.markAllSeen(broadcastSmsCategory)
            subscribeUi(broadcastSmsCategory)
            setItemMenuChecked(broadcastSmsCategory)
        } else {
            subscribeUi(Contact.PRIMARY)
        }
        setBottomNavigation()
    }

    private fun setLinearLayout() {
        llm = LinearLayoutManager(this)
        llm?.orientation = LinearLayoutManager.VERTICAL
        binding?.appBarMain?.contentMain?.smsList?.layoutManager = llm
        binding!!.appBarMain.contentMain.smsList.setHasFixedSize(true)
        smsAdapter = SMSAdapter(listOf(), selectedItem)
        binding!!.appBarMain.contentMain.smsList.adapter = smsAdapter
        val mainActivityHelper = ContextualActionManager()
        mainActivityHelper.contextualActionMode(binding!!.appBarMain.contentMain.smsList, binding!!.appBarMain.contentMain.fab, binding!!.appBarMain.contentMain.bottomNavigation, smsAdapter, this, this, "mainActivity", listOfItem)
    }

    private fun setToolbar() {
        binding!!.appBarMain.toolbar.setTitle(R.string.title_primary)
        setSupportActionBar(binding!!.appBarMain.toolbar)
    }

    private fun subscribeUi(category: Int?) {
        if (category != null) {
            encryptedSharedPreference.edit().putInt(getString(R.string.dialog_option), category).apply()
        }
        mainViewModel.getMessageListByCategory(category).observe(this, { setMessageList(it, category) })
    }

    fun clickFab(view: View?) {
        if (!MessageUtils.checkIfDefaultSms(this)) {
            val intent = Intent(Telephony.Sms.Intents.ACTION_CHANGE_DEFAULT)
            intent.putExtra(Telephony.Sms.Intents.EXTRA_PACKAGE_NAME, this.packageName)
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
            }
        }
    }

    private fun launchPickContact() {
        val intent = Intent(this, SelectContactActivity::class.java)
        startActivity(intent)
    }

    private fun launchCompleteSmsActivity(data: Intent?) {
        val number: String = data?.let { mainViewModel.pickContactSelected(it) } ?: ""
        val intent = Intent(this, CompleteSmsActivity::class.java)
        intent.putExtra(this.getString(R.string.address_id), number)
        startActivity(intent)
    }

    private fun setBottomNavigation() {
//    bottomNavigationView = (BottomNavigationView) findViewById(R.id.bottom_navigation);
//        BottomNavigationViewHelper.disableShiftMode(binding!!.appBarMain.contentMain.bottomNavigation)
        binding!!.appBarMain.contentMain.bottomNavigation.setOnNavigationItemSelectedListener { item: MenuItem ->
            when (item.itemId) {
                R.id.bottomNav_primary -> {
                    encryptedSharedPreference!!.edit().putInt(getString(R.string.dialog_option), Contact.PRIMARY).apply()
                    subscribeUi(Contact.PRIMARY)
                    binding!!.appBarMain.toolbar.setTitle(R.string.title_primary)
                }
                R.id.bottomNav_finance -> {
                    encryptedSharedPreference!!.edit().putInt(getString(R.string.dialog_option), Contact.FINANCE).apply()
                    subscribeUi(Contact.FINANCE)
                    binding!!.appBarMain.toolbar.setTitle(R.string.title_finance)
                }
                R.id.bottomNav_promotion -> {
                    encryptedSharedPreference!!.edit().putInt(getString(R.string.dialog_option), Contact.PROMOTIONS).apply()
                    subscribeUi(Contact.PROMOTIONS)
                    binding!!.appBarMain.toolbar.setTitle(R.string.title_promotions)
                }
                R.id.bottomNav_updates -> {
                    encryptedSharedPreference!!.edit().putInt(getString(R.string.dialog_option), Contact.UPDATES).apply()
                    subscribeUi(Contact.UPDATES)
                    binding!!.appBarMain.toolbar.setTitle(R.string.title_updates)
                }
            }
            true
        }
    }

    private fun setMessageList(messageList: List<Message>, category: Int?) {
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

    private fun setItemMenuChecked(category: Int?) {
        if (category != null) {
            encryptedSharedPreference!!.edit().putInt(getString(R.string.dialog_option), category).apply()
        }
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
        when (item.itemId) {
            R.id.menu_share -> {
                val shareIntent = Intent(Intent.ACTION_SEND)
                shareIntent.type = "text/plain"
                var message = getString(R.string.share_message)
                message += Html.fromHtml(getString(R.string.playstore_link))
                shareIntent.putExtra(Intent.EXTRA_TEXT, message)
                startActivity(Intent.createChooser(shareIntent, getString(R.string.send_to)))
            }
            R.id.menu_rate_us -> {
                val rateIntent = Intent(Intent.ACTION_VIEW,
                        Uri.parse(getString(R.string.playstore_link)))
                startActivity(rateIntent)
            }
            R.id.menu_settings -> {
                navigateTo<SettingsActivity>()
            }
            R.id.menu_search_msg_id -> {
                navigateTo<SearchActivity>()
            }
            R.id.menu_blocked -> {
                navigateTo<BlockedMessageActivity>()
            }
            R.id.menu_archive -> {
                navigateTo<ArchiveMessageActivity>()
            }
            R.id.menu_mark_all_read -> {
            }
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

        //        @JvmField
//        var db: MessageDatabase? = null
        private const val PICK_CONTACT_REQUEST_CODE = 12
        const val WIDGET_UPDATE_DB_COLUMN_KEY = "updateWidgetDb"
        const val MAINACTIVTY_CATEGORY_TASKSTACK_KEY = "category"

    }
}