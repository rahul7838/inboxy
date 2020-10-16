package `in`.smslite.others

import `in`.smslite.R
import `in`.smslite.contacts.Contact
import `in`.smslite.databinding.CustomDialogBinding
import `in`.smslite.db.Message
import `in`.smslite.utils.ThreadUtils
import android.app.Dialog
import android.content.Context
import android.os.Bundle
import android.preference.PreferenceManager
import android.view.ActionMode
import android.view.View

/**
 * Created by rahul1993 on 5/31/2018.
 */
class CustomDialog(context: Context, private val selectedItem: MutableList<Message>, private val callback: ActionMode.Callback, private val whichActivity: String) : Dialog(context) {
    private var checked = false
    private var presentCategory = 0
    private var binding: CustomDialogBinding? = null
    override fun onCreate(savedInstanceState: Bundle) {
        super.onCreate(savedInstanceState)
        binding = CustomDialogBinding.inflate(layoutInflater)
        setContentView(binding!!.root)
        dialogOptionClickListener()
        setVisibility()
        checked = binding!!.dialogCheckbox.isChecked
        PreferenceManager.getDefaultSharedPreferences(context).edit().putBoolean(context.resources.getString(R.string.dialog_checkbox), checked).apply()
        binding!!.dialogHeading1.text = "Move " + selectedItem.size + " conversation to"
    }

    private fun setVisibility() {
        presentCategory = PreferenceManager.getDefaultSharedPreferences(context).getInt(context.getString(R.string.dialog_option), Contact.PRIMARY)
        when (presentCategory) {
            Contact.PRIMARY -> {
                binding!!.dialogOptionPrimary.visibility = View.GONE
                binding!!.dialogOptionPrimaryImage.visibility = View.GONE
            }
            Contact.FINANCE -> {
                binding!!.dialogOptionFinance.visibility = View.GONE
                binding!!.dialogOptionFinanceImage.visibility = View.GONE
            }
            Contact.PROMOTIONS -> {
                binding!!.dialogOptionPromotion.visibility = View.GONE
                binding!!.dialogOptionPromotionImage.visibility = View.GONE
            }
            Contact.UPDATES -> {
                binding!!.dialogOptionUpdate.visibility = View.GONE
                binding!!.dialogOptionUpdateImage.visibility = View.GONE
            }
            Contact.ARCHIVE -> {
                binding!!.dialogOptionArchive.visibility = View.GONE
                binding!!.dialogOptionArchiveImage.visibility = View.GONE
            }
            Contact.BLOCKED -> {
                binding!!.dialogOptionBlocked.visibility = View.GONE
                binding!!.dialogOptionBlockedImage.visibility = View.GONE
            }
            else -> {
            }
        }
    }

    private fun dialogOptionClickListener() {
        binding!!.dialogOptionBlocked.setOnClickListener { v: View? ->
            val category = Contact.BLOCKED
            checked = binding!!.dialogCheckbox.isChecked
            //      int length = selectedItem.size();
//      for (int i = 0; i < length; i++) {
            ThreadUtils.UpdateMessageCategory(context, selectedItem, category, presentCategory, checked).run()
            //      }
            callback.onDestroyActionMode(MainActivityHelper.mActionMode)
            onBackPressed()
        }
        binding!!.dialogOptionArchive.setOnClickListener { v: View? -> startThreadToUpdateCategory(Contact.ARCHIVE) }
        binding!!.dialogOptionPrimary.setOnClickListener { v: View? -> startThreadToUpdateCategory(Contact.PRIMARY) }
        binding!!.dialogOptionFinance.setOnClickListener { v: View? -> startThreadToUpdateCategory(Contact.FINANCE) }
        binding!!.dialogOptionPromotion.setOnClickListener { v: View? -> startThreadToUpdateCategory(Contact.PROMOTIONS) }
        binding!!.dialogOptionUpdate.setOnClickListener { v: View? -> startThreadToUpdateCategory(Contact.UPDATES) }
        binding!!.dialogCancel.setOnClickListener { v: View? ->
            onBackPressed()
            callback.onDestroyActionMode(MainActivityHelper.mActionMode)
        }
    }

    private fun startThreadToUpdateCategory(category: Int) {
        checked = binding!!.dialogCheckbox.isChecked
        ThreadUtils.UpdateMessageCategory(context, selectedItem, category, presentCategory, checked).run()
        onBackPressed()
        callback.onDestroyActionMode(MainActivityHelper.mActionMode)
    }

    override fun onDetachedFromWindow() {
        super.onDetachedFromWindow()
        selectedItem.clear()
    }
}