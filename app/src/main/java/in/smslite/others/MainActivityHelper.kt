package `in`.smslite.others

import `in`.smslite.R
import `in`.smslite.activity.ArchiveMessageActivity
import `in`.smslite.activity.BlockedMessageActivity
import `in`.smslite.activity.CompleteSmsActivity
import `in`.smslite.activity.MainActivity
import `in`.smslite.adapter.SMSAdapter
import `in`.smslite.db.Message
import `in`.smslite.utils.MessageUtils
import android.annotation.SuppressLint
import android.app.Activity
import android.app.AlertDialog
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.provider.Telephony
import android.util.Log
import android.view.ActionMode
import android.view.Menu
import android.view.MenuItem
import android.view.View
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.floatingactionbutton.FloatingActionButton
import java.util.*

/**
 * Created by rahul1993 on 5/8/2018.
 */
class MainActivityHelper {
    private var recyclerView: RecyclerView? = null
    private var fab: FloatingActionButton? = null
    private var bottomNavigationView: BottomNavigationView? = null
    private var smsAdapter1: SMSAdapter? = null
    private var context: Context? = null
    private var address: String? = null
    private val selectedItem: MutableList<Message> = ArrayList()
    private var activity: Activity? = null
    private var whichActivity: String? = null
    private var listOfItem: List<Message> = ArrayList()

    //  public MainActivityHelper(Context context)
    fun contextualActionMode(recyclerView: RecyclerView, fab: FloatingActionButton,
                             bottomNavigationView: BottomNavigationView, smsAdapter: SMSAdapter?,
                             activity: Activity?, context: Context, string: String?, listOfItem: List<Message>) {
        this.fab = fab
        this.bottomNavigationView = bottomNavigationView
        this.context = context
        this.recyclerView = recyclerView
        smsAdapter1 = smsAdapter
        this.activity = activity
        whichActivity = string
        this.listOfItem = listOfItem
        recyclerView.addOnItemTouchListener(RecyclerItemClickListener(context, recyclerView, object : RecyclerItemClickListener.OnItemClickListener {
            override fun onItemClick(view: View?, position: Int) {
                if (isMultiSelect) {
                    address = smsAdapter1?.messages?.get(position)?.getAddress()
                    multi_select(position, mActionModeCallback, selectedItem, listOfItem, address)
                    refreshAdapter()
                } else {
                    address = smsAdapter1?.messages?.get(position)?.getAddress()
                    val i = Intent(context, CompleteSmsActivity::class.java)
                    i.putExtra(view?.resources?.getString(R.string.address_id), address)
                    //          TODO:check line 86 category
//          i.putExtra("category", smsAdapter1.messages.get(position).getCategory());
                    context.startActivity(i)
                }
            }

            @SuppressLint("RestrictedApi")
            override fun onItemLongClick(view: View?, position: Int) {
                Log.d(TAG, Integer.toString(listOfItem.size) + listOfItem[0].getAddress())
                if (!isMultiSelect) {
                    bottomNavigationView.visibility = View.GONE
                    fab.visibility = View.GONE
                    address = smsAdapter1?.messages?.get(position)?.getAddress()
                    isMultiSelect = true
                    if (mActionMode == null) {
                        if (whichActivity == "blocked") {
                            mActionMode = (context as BlockedMessageActivity).startActionMode(mActionModeCallback)
                        } else if (whichActivity == "mainActivity") {
                            mActionMode = (context as MainActivity).startActionMode(mActionModeCallback)
                        } else {
                            mActionMode = (context as ArchiveMessageActivity).startActionMode(mActionModeCallback)
                        }
                    }
                }
                multi_select(position, mActionModeCallback, selectedItem, listOfItem, address)
                refreshAdapter()
            }
        }))
    }

    var mActionModeCallback: ActionMode.Callback = object : ActionMode.Callback {
        override fun onCreateActionMode(mode: ActionMode, menu: Menu): Boolean {
            // Inflate a menu resource providing context menu items
            val inflater = mode.menuInflater
            inflater.inflate(R.menu.menu_multi_select, menu)
            menu.getItem(0).setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS)
            menu.getItem(1).setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS)
            menu.getItem(2).setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS)
            mActionMode = mode
            activity!!.window.statusBarColor = context!!.resources.getColor(R.color.contextual_status_bar_color)
            return true
        }

        override fun onPrepareActionMode(mode: ActionMode, menu: Menu): Boolean {
            return false // Return false if nothing is done
        }

        override fun onActionItemClicked(mode: ActionMode, item: MenuItem): Boolean {
            return when (item.itemId) {
                R.id.action_delete -> {
                    if (MessageUtils.checkIfDefaultSms(context)) {
                        alertDialog(context, this, selectedItem)
                    } else {
                        MessageUtils.setDefaultSms(context)
                    }
                    Log.d(TAG, "Action delete")
                    true
                }
                R.id.action_select_all -> {
                    Log.d(TAG, "select all")
                    selectedItem.clear()
                    selectedItem.addAll(listOfItem)
                    val size = selectedItem.size
                    selectedAddressList.clear()
                    var i = 0
                    while (i < size) {
                        selectedAddressList.add(selectedItem[i].getAddress())
                        i++
                    }
                    mActionMode!!.title = "" + selectedItem.size
                    refreshAdapter()
                    true
                }
                R.id.menu_multi_select_move_to -> {
                    val dialog = CustomDialog(context!!, selectedItem, this, whichActivity!!)
                    dialog.show()
                    false
                }
                else -> false
            }
        }

        override fun onDestroyActionMode(mode: ActionMode) {
            mActionMode!!.finish()
            mActionMode = null
            isMultiSelect = false
            selectedItem.clear()
            smsAdapter1?.notifyDataSetChanged()
            selectedAddressList.clear()
            setVisibility()
            activity!!.window.statusBarColor = context!!.resources.getColor(R.color.colorPrimaryDark)
            refreshAdapter()
        }
    }

    @SuppressLint("RestrictedApi")
    private fun setVisibility() {
        if (whichActivity == "blocked" || whichActivity == "archive") {
            fab!!.visibility = View.GONE
            bottomNavigationView!!.visibility = View.GONE
        } else {
            fab!!.visibility = View.VISIBLE
            bottomNavigationView!!.visibility = View.VISIBLE
        }
    }

    private fun refreshAdapter() {
        smsAdapter1?.selectedItemAdapter = selectedItem
        smsAdapter1?.listOfItemAdapter = listOfItem
        smsAdapter1?.notifyDataSetChanged()
    }

    companion object {
        private val TAG = MainActivityHelper::class.java.simpleName
        var mActionMode: ActionMode? = null
        var isMultiSelect = false
        var selectedAddressList: MutableList<String?> = ArrayList()
        private fun multi_select(position: Int, callback: ActionMode.Callback, selectedItem: MutableList<Message>,
                                 listOfItem: List<Message>, address: String?) {
            if (mActionMode != null) {
                if (selectedItem.contains(listOfItem[position])) {
                    selectedItem.remove(listOfItem[position])
                    selectedAddressList.remove(address)
                } else {
                    selectedItem.add(listOfItem[position])
                    selectedAddressList.add(address)
                }
                if (selectedItem.size > 0) {
                    mActionMode!!.title = "" + selectedItem.size
                } else {
                    callback.onDestroyActionMode(mActionMode)
                }
            }
        }

        private fun alertDialog(context: Context?, callback: ActionMode.Callback, selectedItem: List<Message>) {
            deleteDialog(context, selectedItem) { dialog: DialogInterface?, which: Int ->
                when (which) {
                    DialogInterface.BUTTON_POSITIVE -> {
                        val size = selectedAddressList.size
                        var i = 0
                        while (i < size) {

//              All the messages having same address will get deleted from below query.
                            MainActivity.localMessageDbViewModel!!.deleteSelectedConversation(selectedAddressList[i])
                            val where = Telephony.TextBasedSmsColumns.ADDRESS + " LIKE ?"
                            val arg = arrayOf(selectedAddressList[i])
                            val modifiedRows = context!!.contentResolver.delete(Telephony.Sms.CONTENT_URI, where, arg)
                            Log.d(TAG, "Integer.toString(count)" + " count " + Integer.toString(modifiedRows) + " modified rows")
                            i++
                        }
                        callback.onDestroyActionMode(mActionMode)
                    }
                    DialogInterface.BUTTON_NEGATIVE -> callback.onDestroyActionMode(mActionMode)
                }
            }
        }

        private fun deleteDialog(context: Context?, selectedItem: List<Message>, clicked: DialogInterface.OnClickListener) {
            AlertDialog.Builder(context)
                    .setMessage("Are you sure you would like to delete Convsersations?")
                    .setTitle("Delete " + selectedItem.size + " conversations")
                    .setPositiveButton("YES", clicked)
                    .setNegativeButton("CANCEL", clicked)
                    .create()
                    .show()
        }
    }
}