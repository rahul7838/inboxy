package `in`.smslite.activity

import `in`.smslite.R
import `in`.smslite.SMSApplication
import `in`.smslite.adapter.CompleteSmsAdapter
import `in`.smslite.contacts.Contact
import `in`.smslite.contacts.PhoneContact
import `in`.smslite.databinding.ActivitySmsCompleteBinding
import `in`.smslite.db.Message
import `in`.smslite.others.CompleteSmsActivityHelper
import `in`.smslite.utils.*
import `in`.smslite.viewHolder.CompleteSmsSentViewHolder
import `in`.smslite.viewModel.CompleteSmsActivityViewModel
import android.Manifest
import android.annotation.TargetApi
import android.app.PendingIntent
import android.content.*
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.os.Process
import android.preference.PreferenceManager
import android.provider.ContactsContract
import android.provider.Settings
import android.provider.Telephony
import android.telephony.SmsManager
import android.telephony.SubscriptionInfo
import android.telephony.SubscriptionManager
import android.telephony.SubscriptionManager.OnSubscriptionsChangedListener
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.EditText
import android.widget.PopupMenu
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.lifecycle.LiveData
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.LinearLayoutManager
import java.io.UnsupportedEncodingException
import java.net.URLDecoder

class CompleteSmsActivity : AppCompatActivity(), PopupMenu.OnMenuItemClickListener, CompleteSmsAdapter.SendTextSms {
    var editText: EditText? = null
    val MY_PERMISSIONS_REQUEST_CALL_PHONE = 1
    private val MY_PERMISSION_REQUEST_READ_PHONE_STATE = 2
    var address: String? = null
    private val messageListByAddress: LiveData<List<Message>>? = null
    private val observer: Observer<List<Message>>? = null
    var listOfItem: MutableList<Message> = ArrayList()
    var completeSmsAdapter: CompleteSmsAdapter? = null
    private var category = 0
    private var isDualSim = 0
    private val subscriptionInfoList: List<SubscriptionInfo>? = null
    private var binding: ActivitySmsCompleteBinding? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        completeSmsActivityViewModel = ViewModelProviders.of(this).get<CompleteSmsActivityViewModel>(CompleteSmsActivityViewModel::class.java)
        if (intent.data != null) {
            dataFromOtherAppIntent
            category = completeSmsActivityViewModel?.findFutureCategory(address) ?: 0
            category = if (category != 0) {
                completeSmsActivityViewModel?.findFutureCategory(address) ?: 0
            } else {
                ContactUtils.getContact(address, this, true).category
            }
        } else {
            val bundle = intent.extras!!
            address = bundle.getString(getString(R.string.address_id))
            category = PreferenceManager.getDefaultSharedPreferences(this).getInt(getString(R.string.dialog_option), ConstantUtils.BUNDLE_FROM_PENDING_INTENT)
        }
        PhoneContact.initialize(this)
        contact = ContactUtils.getContact(address, this, true)
        category = contact!!.category
        address = if (contact!!.category == Contact.PRIMARY) {
            ContactUtils.normalizeNumber(address)
        } else {
            contact!!.number
        }
        Log.d(TAG, address + address)
        ThreadUtils.UpdateDbNotiClickedThread(address!!).run()
        binding = ActivitySmsCompleteBinding.inflate(layoutInflater)
        setContentView(binding!!.root)
        editText = findViewById<View>(R.id.reply_sms_edit_text_box_id) as EditText
        val llm = LinearLayoutManager(applicationContext)
        llm.orientation = LinearLayoutManager.VERTICAL
        llm.stackFromEnd = true
        binding!!.completeSmsRecyclerView.recyclerView.layoutManager = llm
        binding!!.completeSmsRecyclerView.recyclerView.setHasFixedSize(true)
        setToolbar()
        sendButtonClickListener()
        val messages: List<Message> = ArrayList()
        completeSmsAdapter = address?.let { CompleteSmsAdapter(messages, it, this, selectedItem, listOfItem) }
        binding!!.completeSmsRecyclerView.recyclerView.adapter = completeSmsAdapter
        val completeSmsActivityHelper = CompleteSmsActivityHelper()
        completeSmsActivityHelper.contextualActionMode(binding!!.completeSmsRecyclerView.recyclerView, this, this, completeSmsAdapter!!, listOfItem)
        telephonyInfo
        subscribeUi()
    }

    //      String locale = context.getResources().getConfiguration().locale.getCountry();
    val dataFromOtherAppIntent: Unit
        get() {
            val uri = intent.data!!
            val data = uri.toString()
            Log.d(TAG, data)
            val schema = intent.data!!.scheme
            address = if (schema!!.startsWith("smsto") || schema.startsWith("mmsto")) {
                data.replace("smsto:", "").replace("mmsto:", "")
            } else {
                data.replace("sms:", "").replace("mms:", "")
            }
            //      String locale = context.getResources().getConfiguration().locale.getCountry();
            try {
                address = URLDecoder.decode(address, "UTF-8")
            } catch (e: UnsupportedEncodingException) {
                e.printStackTrace()
            }
            address = ContactUtils.normalizeNumber(address)
        }

    fun onClickAttachContact(view: View?) {
        if (!MessageUtils.checkIfDefaultSms(this)) {
            val intent = Intent(Telephony.Sms.Intents.ACTION_CHANGE_DEFAULT)
            intent.putExtra(Telephony.Sms.Intents.EXTRA_PACKAGE_NAME, this.packageName)
            startActivityForResult(intent, ConstantUtils.NOT_DEFAULT_SMS_APP)
        } else {
            attachContact()
        }
    }

    private fun attachContact() {
        val pickContactIntent = Intent(Intent.ACTION_PICK, Uri.parse("content://contacts"))
        pickContactIntent.type = ContactsContract.CommonDataKinds.Phone.CONTENT_TYPE // Show user only contacts w/ phone numbers
        startActivityForResult(pickContactIntent, ConstantUtils.ON_ATTACH_CONTACT_CLICK)
    }

    private fun addTextToMessage(data: Intent?) {
        val nameAndNumber: List<String> = data?.let { completeSmsActivityViewModel?.queryDataToFindConatact(it) }
                ?: arrayListOf()
        val name = nameAndNumber[0]
        val number = nameAndNumber[1]
        val contact = "Name: $name\nPhone: $number"
        editText!!.setText(contact)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == SEND_TEXT_SMS_REQUEST) {
            if (resultCode == RESULT_OK) {
                val l: Long = 0
                sendTextSms(l, address, category)
            }
        }
        if (requestCode == ConstantUtils.NOT_DEFAULT_SMS_APP) {
            if (resultCode == RESULT_OK) {
                attachContact()
            }
        }
        if (requestCode == ConstantUtils.ON_ATTACH_CONTACT_CLICK) {
            if (resultCode == RESULT_OK) {
                addTextToMessage(data)
            }
        }
    }

    private fun sendButtonClickListener() {
        binding!!.completeSmsRecyclerView.sendButtonId.setOnClickListener { v ->
            if (Build.VERSION.SDK_INT > Build.VERSION_CODES.N_MR1) {
                checkPhoneStatePermission()
            } else {
                if (address?.matches(Regex("[a-zA-Z-]*")) == true) {
                    Toast.makeText(v.context, "Invalid address", Toast.LENGTH_SHORT).show()
                } else {
                    sendButtonClicked()
                }
            }
        }
    }

    private fun checkPhoneStatePermission() {
        val permissionCheckCall = ContextCompat.checkSelfPermission(this@CompleteSmsActivity,
                Manifest.permission.READ_PHONE_STATE)
        if (permissionCheckCall != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this@CompleteSmsActivity, arrayOf(Manifest.permission.READ_PHONE_STATE), MY_PERMISSION_REQUEST_READ_PHONE_STATE)
        } else {
            sendButtonClicked()
        }
    }

    fun subscribeUi() {
        completeSmsActivityViewModel?.getMessageListByAddress(address, category)?.observe(this, Observer { messages: List<Message>? -> showUi(messages) })
    }

    fun showUi(messages: List<Message>?) {
        listOfItem.clear()
        listOfItem.addAll(messages!!)
        completeSmsAdapter?.setMessage(messages)
    }

    private fun sendButtonClicked() {
        if (Telephony.Sms.getDefaultSmsPackage(this) != this.packageName) {
            val intent = Intent(Telephony.Sms.Intents.ACTION_CHANGE_DEFAULT)
            intent.putExtra(Telephony.Sms.Intents.EXTRA_PACKAGE_NAME, this.packageName)
            startActivityForResult(intent, SEND_TEXT_SMS_REQUEST)
        } else {
            val l: Long = 0
            sendTextSms(l, address, category)
        }
    }

    //  Dual sim selection popup menu
    @TargetApi(22)
    fun popupMenu(view: View?) {
        invalidateOptionsMenu()
        val popup = PopupMenu(this, binding!!.completeSmsRecyclerView.completeSmsSimOption)
        popup.menuInflater.inflate(R.menu.menu_sim_option, popup.menu)
        //    getTelephonyInfo();
        val sim2 = popup.menu.findItem(R.id.menu_sim2_option)
        val sim1 = popup.menu.findItem(R.id.menu_sim1_option)
        val subId1 = subscriptionInfoList!![0]
        val subId2: SubscriptionInfo
        if (isDualSim != 2) {
            sim2.isVisible = false
            sim1.title = subId1.displayName
        } else {
            subId2 = subscriptionInfoList[1]
            sim1.title = subId1.displayName.toString() + " -sim" + Integer.toString(subId1.simSlotIndex + 1)
            sim2.title = subId2.displayName.toString() + " -sim" + Integer.toString(subId2.simSlotIndex + 1)
        }
        popup.show()
        popup.setOnMenuItemClickListener(this)
    }

    //    subscriptionInfoList = subscriptionManager.getActiveSubscriptionInfoList();
    @get:TargetApi(22)
    val telephonyInfo:
    //    smsManager = SmsManager.getDefault();
            Unit
        get() {
            val subscriptionManager = this.getSystemService(TELEPHONY_SUBSCRIPTION_SERVICE) as SubscriptionManager
            subscriptionManager.addOnSubscriptionsChangedListener(SubscriptionChangeListener())
            //    subscriptionInfoList = subscriptionManager.getActiveSubscriptionInfoList();
            val dualSim = subscriptionManager.activeSubscriptionInfoCountMax
            if (dualSim == 2) {
                isDualSim = 2
            }
            //    smsManager = SmsManager.getDefault();
            smsManager = SmsManager.getSmsManagerForSubscriptionId(SmsManager.getDefaultSmsSubscriptionId())
        }

    override fun onSendTextSms(time: Long?, address: String?, category: Int) {}

    @TargetApi(Build.VERSION_CODES.LOLLIPOP_MR1)
    inner class SubscriptionChangeListener : OnSubscriptionsChangedListener() {
        override fun onSubscriptionsChanged() {
            super.onSubscriptionsChanged()
            smsManager = SmsManager.getSmsManagerForSubscriptionId(SmsManager.getDefaultSmsSubscriptionId())
        }
    }

    @TargetApi(22)
    override fun onMenuItemClick(item: MenuItem): Boolean {
        val id = item.itemId
        when (id) {
            R.id.menu_sim1_option -> smsManager = SmsManager.getSmsManagerForSubscriptionId(subscriptionInfoList!![0].subscriptionId)
            R.id.menu_sim2_option -> if (isDualSim == 2) {
                smsManager = SmsManager.getSmsManagerForSubscriptionId(subscriptionInfoList!![1].subscriptionId)
            }
            else -> {
            }
        }
        return true
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.activity_completesms, menu)
        return true
    }

    fun sendTextSms(time: Long?, address: String?, category: Int) {
        val msg: String
        if (CompleteSmsSentViewHolder.tryFailedSms) {
            CompleteSmsSentViewHolder.tryFailedSms = false
            msg = MainActivity.db?.messageDao()?.getFailedSmsText(time) ?: ""
            MainActivity.db?.messageDao()?.deleteFailedMsg(time)
            thread(msg, address, category).start()
        } else {
            val editableText = editText!!.text
            msg = editableText.toString()
            //    if (!msg.isEmpty()) {
            editableText.clear()
            //      write sent sms to local database
            thread(msg, address, category).start()
        }
    }

    private class thread  //
    //    }
    internal constructor(var msg: String, var address: String?, var category: Int) : Thread() {
        override fun run() {
            super.run()
            if (!msg.isEmpty()) {
                timeStampForBroadCast = System.currentTimeMillis()
                message = Message()
                message!!.address = address
                Log.d(TAG, address!!)
                message!!.body = msg
                message!!.read = true
                message!!.seen = true
                message!!.category = category
                message!!.threadId = 0
                message!!.timestamp = timeStampForBroadCast!!
                Log.d(TAG, "message.timeStamp " + java.lang.Long.toString(timeStampForBroadCast!!))
                message!!.type = Message.MessageType.QUEUED
                MainActivity.db?.messageDao()?.insertMessage(message)
                val sentIntent = Intent()
                //        sentIntent.putExtra("timeStamp123", timeStampForBroadCast);
                sentIntent.action = "in.smslite.SEND_SMS_ACTION"
                val sentPendingIntent = PendingIntent.getBroadcast(SMSApplication.application, SMS_SEND_INTENT_REQUEST, sentIntent, PendingIntent.FLAG_CANCEL_CURRENT)
                val deliveredIntent = Intent()
                deliveredIntent.action = "in.smslite.DELIVERED_SMS_ACTION"
                //        deliveredIntent.putExtra("deliveredSms", "yes");
                deliveredIntent.putExtra("timeStamp123", timeStampForBroadCast)
                val requestCode = timeStampForBroadCast!!.toInt()
                val deliveredPendingIntent = PendingIntent.getBroadcast(SMSApplication.application, requestCode, deliveredIntent, PendingIntent.FLAG_ONE_SHOT)
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP_MR1) {
                    if (smsManager != null) {
                        smsManager!!.sendTextMessage(address, null, msg, sentPendingIntent, deliveredPendingIntent)
                    }
                } else {
                    val smsManager = SmsManager.getDefault()
                    smsManager.sendTextMessage(address, null, msg, sentPendingIntent, deliveredPendingIntent)
                }
            } else {
                val handler: Handler? = SMSApplication.application?.getMainLooper()?.let { Handler(it) }
                val task = Runnable { Toast.makeText(SMSApplication.application, "Please write some text!", Toast.LENGTH_SHORT).show() }
                handler?.post(task)
            }
        } //    public void dual sim(){
    }

    private val SendSmsBroadcastReceiver: BroadcastReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            when (resultCode) {
                RESULT_OK -> {
                    //          CompleteSmsSentViewHolder.smsStatusVisiblity("sent");
                    Log.d(TAG, "sent sms successful")
                    //      write sent sms to content provider
//          Long timeStamp = intent.getLongExtra("timeStamp123", 0);
//          Log.d(TAG, "timeStamp " + Long.toString(timeStamp));
                    message!!.type = Message.MessageType.SENT
                    MainActivity.db?.messageDao()?.updateSentSuccessful(timeStampForBroadCast)
                    ContentProviderUtil.writeSentSms(message, context)
                }
                SmsManager.RESULT_ERROR_NULL_PDU -> //          CompleteSmsSentViewHolder.smsStatusVisiblity("Not sent");
                    Log.d(TAG, "null pdu code")
                else -> {
                    Log.d(TAG, "default code")
                    message!!.type = Message.MessageType.FAILED
                    MainActivity.db?.messageDao()?.updateSentFailedSms(message!!.timestamp)
                }
            }
        }
    }

    fun setToolbar() {
        setSupportActionBar(binding!!.toolbar)
        val bar = supportActionBar
        bar?.setDisplayHomeAsUpEnabled(true)
        title = contact!!.displayName
    }

    /*
    public List<Message> getSmsList(String address) {

      return MessageDatabase.messageDao().getMessageListByAddress();
    }
  */
    override fun onResume() {
        super.onResume()
        val sentSmsIntentFilter = IntentFilter()
        sentSmsIntentFilter.addAction("in.smslite.SEND_SMS_ACTION")
        registerReceiver(SendSmsBroadcastReceiver, sentSmsIntentFilter)
    }

    override fun onDestroy() {
        super.onDestroy()
        unregisterReceiver(SendSmsBroadcastReceiver)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        val id = item.itemId
        if (id == R.id.call_icon) {
            checkCallPermission()
        } else if (id == R.id.contact_details) {
            if (Contact.Source.PHONE == contact!!.source && contact!!.number != contact!!.displayName) {
                val phoneContact: PhoneContact? = contact as PhoneContact?
                Log.d(TAG, phoneContact.toString())
                ContactsContract.QuickContact.showQuickContact(this@CompleteSmsActivity, binding!!.coordinatorLayout,
                        phoneContact?.uri,
                        ContactsContract.QuickContact.MODE_LARGE, null)
            } else {
                val intent = Intent(ContactsContract.Intents.SHOW_OR_CREATE_CONTACT,
                        Uri.fromParts("tel", contact!!.number, null))
                this@CompleteSmsActivity.startActivity(intent)
            }
        } else if (id == R.id.home) {
            onBackPressed()
        }
        return true
    }

    fun checkCallPermission() {
        val permissionCheckCall = ContextCompat.checkSelfPermission(this@CompleteSmsActivity,
                Manifest.permission.CALL_PHONE)
        if (permissionCheckCall != PackageManager.PERMISSION_GRANTED) {
            requestPermissions()
        } else {
            performCalling()
        }
    }

    private fun requestPermissions() {
        ActivityCompat.requestPermissions(this@CompleteSmsActivity, arrayOf(Manifest.permission.CALL_PHONE), MY_PERMISSIONS_REQUEST_CALL_PHONE)
    }

    override fun onRequestPermissionsResult(requestCode: Int,
                                            permissions: Array<String>, grantResults: IntArray) {
        when (requestCode) {
            MY_PERMISSIONS_REQUEST_CALL_PHONE ->         // If request is cancelled, the result arrays are empty.
                if (grantResults.size > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    performCalling()
                } else {
                    if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.CALL_PHONE)) {
                        val msg = "Phone permission required for this app to perform the action"
                        explainPermissionDialog(msg)
                    } else {
                        openSetting()
                    }
                }
            MY_PERMISSION_REQUEST_READ_PHONE_STATE -> if (grantResults.size > 0
                    && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                sendButtonClicked()
            } else {
                if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.READ_PHONE_STATE)) {
                    val msg = "Phone permission required for this app to perform the action"
                    explainPermissionDialog(msg)
                } else {
                    openSetting()
                }
            }
        }
    }

    private fun openSetting() {
        val intent = Intent()
        intent.action = Settings.ACTION_APPLICATION_DETAILS_SETTINGS
        val uri = Uri.fromParts("package", packageName, null)
        intent.data = uri
        startActivity(intent)
    }

    private fun explainPermissionDialog(msg: String) {
        showDialogOK(msg) { dialogInterface, which ->
            when (which) {
                DialogInterface.BUTTON_POSITIVE -> checkCallPermission()
                DialogInterface.BUTTON_NEGATIVE -> {
                }
            }
        }
    }

    private fun showGotoSettings() {
        // TODO: Should directly open app settings page
        val alertDialogBuilder = AlertDialog.Builder(this@CompleteSmsActivity)
        alertDialogBuilder.setTitle("Click ok to Exit")
        alertDialogBuilder.setMessage("Go to settings --> Apps and enable permissions")
        alertDialogBuilder.setPositiveButton("Ok") { dialogInterface, i ->
            Process.killProcess(Process.myPid())
            System.exit(1)
        }
                .create()
                .show()
    }

    private fun showDialogOK(message: String, okListener: DialogInterface.OnClickListener) {
        AlertDialog.Builder(this@CompleteSmsActivity)
                .setMessage(message)
                .setPositiveButton("OK", okListener)
                .setNegativeButton("CANCEL", okListener)
                .create()
                .show()
    }

    private fun performCalling() {
        val callIntent = Intent(Intent.ACTION_CALL)
        contact!!.number?.let { Log.d(TAG, it) }
        callIntent.data = Uri.parse("tel:" + contact!!.number)
        startActivity(callIntent)
    }

    companion object {
        private val TAG = CompleteSmsActivity::class.java.simpleName
        private const val SMS_SEND_INTENT_REQUEST = 100
        private const val SMS_DELIVER_INTENT_REQUEST = 101
        private const val SEND_TEXT_SMS_REQUEST = 102
        var contact: Contact? = null
        var message: Message? = null
        var timeStampForBroadCast: Long? = null

        @JvmField
        var completeSmsActivityViewModel: CompleteSmsActivityViewModel? = null

        @JvmField
        var selectedItem: ArrayList<Message> = ArrayList()
        private var smsManager: SmsManager? = null
    }
}