package `in`.smslite.others

import `in`.smslite.R
import `in`.smslite.activity.CompleteSmsActivity
import `in`.smslite.activity.MainActivity
import `in`.smslite.adapter.CompleteSmsAdapter
import `in`.smslite.db.Message
import `in`.smslite.utils.MessageUtils
import android.app.Activity
import android.app.AlertDialog
import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.DialogInterface
import android.provider.Telephony
import android.util.Log
import android.view.ActionMode
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import java.util.*

/**
 * Created by rahul1993 on 5/13/2018.
 */
class CompleteSmsActivityHelper {
    private var mContext: Context? = null
    private var activity: Activity? = null
    private var completeSmsAdapter: CompleteSmsAdapter? = null
    private var listOfItem: List<Message>? = null
    fun contextualActionMode(recyclerView: RecyclerView, context: Context, activity: Activity?,
                             completeSmsAdapter: CompleteSmsAdapter, messages: List<Message>?) {
        mContext = context
        this.activity = activity
        this.completeSmsAdapter = completeSmsAdapter
        listOfItem = messages
        recyclerView.addOnItemTouchListener(RecyclerItemClickListener(context, recyclerView, object : RecyclerItemClickListener.OnItemClickListener {
            override fun onItemClick(view: View?, position: Int) {
                if (isMultiSelect) {
                    timeStamp = completeSmsAdapter.smsConversation.get(position).getTimestamp()
                    if (view != null) {
                        multi_select(position, view)
                    }
                    if (CompleteSmsActivity.selectedItem.size > 1) {
                        mMenu!!.getItem(2).isVisible = false
                    } else if (CompleteSmsActivity.selectedItem.size > 0) {
                        mMenu!!.getItem(2).isVisible = true
                        mMenu!!.getItem(2).setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS)
                    }
                }
            }

            override fun onItemLongClick(view: View?, position: Int) {
                if (!isMultiSelect) {
//          address = smsAdapter.messages.get(position).getAddress();
                    timeStamp = listOfItem!![position].getTimestamp()
                    isMultiSelect = true
                    if (mActionMode == null) {
                        mActionMode = (context as CompleteSmsActivity).startActionMode(mActionModeCallback)
                    }
                }
                view?.let { multi_select(position, it) }
            }
        }))
    }

    private fun multi_select(position: Int, view: View) {
        if (mActionMode != null) {
            if (CompleteSmsActivity.selectedItem.contains(listOfItem!![position])) {
                CompleteSmsActivity.selectedItem.remove(listOfItem!![position])
                selectedTimeStampList.remove(timeStamp)
            } else {
                CompleteSmsActivity.selectedItem.add(listOfItem!![position])
                selectedTimeStampList.add(timeStamp)
            }
            if (CompleteSmsActivity.selectedItem.size > 0) {
                mActionMode!!.title = "" + CompleteSmsActivity.selectedItem.size
            } else {
                mActionModeCallback.onDestroyActionMode(mActionMode)
            }

//
            refreshAdapter()
            //
        }
    }

    private fun refreshAdapter() {
        completeSmsAdapter?.selectedItemAdapter = CompleteSmsActivity.selectedItem
        completeSmsAdapter?.listOfItemAdapter = listOfItem!!
        completeSmsAdapter?.notifyDataSetChanged()
    }

    private val mActionModeCallback: ActionMode.Callback = object : ActionMode.Callback {
        override fun onCreateActionMode(mode: ActionMode, menu: Menu): Boolean {
            // Inflate a menu resource providing context menu items
            val inflater = mode.menuInflater
            inflater.inflate(R.menu.menu_multi_select_complete_sms_activity, menu)
            mActionMode = mode
            mMenu = menu
            mMenu!!.getItem(0).setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS)
            mMenu!!.getItem(1).setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS)
            mMenu!!.getItem(2).setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS)
            //      if(selectedItem.size() > 1) {
//        menu.getItem(2).setVisible(false);
//      }
            activity!!.window.statusBarColor = mContext!!.resources.getColor(R.color.contextual_status_bar_color)
            //      context_menu = menu;
            return true
        }

        override fun onPrepareActionMode(mode: ActionMode, menu: Menu): Boolean {
            return false // Return false if nothing is done
        }

        override fun onActionItemClicked(mode: ActionMode, item: MenuItem): Boolean {
            return when (item.itemId) {
                R.id.action_delete -> {
                    if (MessageUtils.checkIfDefaultSms(mContext)) {
                        alertDialog()
                    } else {
                        MessageUtils.setDefaultSms(mContext)
                    }
                    Log.d(TAG, "Action delete")
                    true
                }
                R.id.action_select_all -> {
                    Log.d(TAG, "select all")
                    CompleteSmsActivity.selectedItem.clear()
                    listOfItem?.let { CompleteSmsActivity.selectedItem.addAll(it) }
                    val size: Int = CompleteSmsActivity.selectedItem.size
                    selectedTimeStampList.clear()
                    var i = 0
                    while (i < size) {
                        selectedTimeStampList.add(CompleteSmsActivity.selectedItem.get(i).getTimestamp())
                        i++
                    }
                    mActionMode!!.title = "" + CompleteSmsActivity.selectedItem.size
                    if (CompleteSmsActivity.selectedItem.size > 1) {
                        mMenu!!.getItem(2).isVisible = false
                    }
                    refreshAdapter()
                    true
                }
                R.id.action_copy -> {
                    Log.d(TAG, CompleteSmsActivity.selectedItem.size.toString() + " body")
                    val body: String = CompleteSmsActivity.selectedItem.get(0).getBody()
                    val clipboard = mContext!!.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                    val clip = ClipData.newPlainText("Copied text", body)
                    clipboard?.setPrimaryClip(clip)
                    Toast.makeText(mContext, "Message copied!", Toast.LENGTH_SHORT).show()
                    mActionMode?.let { onDestroyActionMode(it) }
                    false
                }
                else -> false
            }
        }

        override fun onDestroyActionMode(mode: ActionMode) {
            mActionMode!!.finish()
            createActionMode = true
            mMenu = null
            mActionMode = null
            isMultiSelect = false
            CompleteSmsActivity.selectedItem.clear()
            completeSmsAdapter?.notifyDataSetChanged()
            selectedTimeStampList.clear()
            activity!!.window.statusBarColor = mContext!!.resources.getColor(R.color.colorPrimaryDark)
            refreshAdapter()
        }
    }

    private fun alertDialog() {
        deleteDialog { dialog, which ->
            when (which) {
                DialogInterface.BUTTON_POSITIVE -> {
                    val size = selectedTimeStampList.size
                    var i = 0
                    while (i < size) {
                        Log.d(TAG, java.lang.Long.toString(selectedTimeStampList[i]!!))
                        //              All the messages having same address will get deleted from below query.
                        MainActivity.db?.messageDao()?.deleteSelectedMessage(selectedTimeStampList[i])
                        //              String[] projection = {Telephony.TextBasedSmsColumns.DATE, Telephony.TextBasedSmsColumns.DATE_SENT};
                        val where = (Telephony.TextBasedSmsColumns.DATE + " LIKE ? "
                                + "OR " + Telephony.TextBasedSmsColumns.DATE_SENT + " LIKE ?")
                        val arg = arrayOf(java.lang.Long.toString(selectedTimeStampList[i]!!))
                        //              String[] arg = {"1526307785635"};
                        val modifiedRows = mContext!!.contentResolver.delete(Telephony.Sms.CONTENT_URI, where, arg)
                        //              Cursor cursor = mContext.getContentResolver().query(Telephony.Sms.CONTENT_URI, projection, where, arg,null);
//              int count = 0;
//              if (cursor != null) {
//                count = cursor.getCount();
//              }
//              cursor.moveToFirst();
//              String time = cursor.getString(0);
                        Log.d(TAG, " " + "Integer.toString(count)" + " count " + Integer.toString(modifiedRows) + " modified rows")
                        i++
                    }
                    mActionModeCallback.onDestroyActionMode(mActionMode)
                }
                DialogInterface.BUTTON_NEGATIVE -> mActionModeCallback.onDestroyActionMode(mActionMode)
            }
        }
    }

    private fun deleteDialog(clicked: DialogInterface.OnClickListener) {
        AlertDialog.Builder(mContext)
                .setMessage("Are you sure you would like to delete messages?")
                .setTitle("Delete " + CompleteSmsActivity.selectedItem.size + " messages")
                .setPositiveButton("YES", clicked)
                .setNegativeButton("CANCEL", clicked)
                .create()
                .show()
    }

    companion object {
        private val TAG = CompleteSmsActivityHelper::class.java.simpleName
        private var mActionMode: ActionMode? = null
        private var isMultiSelect = false
        private var timeStamp: Long? = null
        private val selectedTimeStampList: MutableList<Long?> = ArrayList()
        private var mMenu: Menu? = null
        private var createActionMode = true
    }
}