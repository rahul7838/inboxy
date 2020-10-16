package `in`.smslite.viewHolder

import `in`.smslite.R
import `in`.smslite.activity.CompleteSmsActivity
import `in`.smslite.contacts.Contact
import `in`.smslite.contacts.PhoneContact
import `in`.smslite.utils.ContactUtils
import `in`.smslite.utils.TimeUtils
import android.content.Context
import android.content.Intent
import android.graphics.drawable.Drawable
import android.net.Uri
import android.provider.ContactsContract
import android.text.Spanned
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

/**
 * Created by rahul1993 on 4/22/2018.
 */
class SearchSmsViewHolder(private val view: View, private val mContext: Context) : RecyclerView.ViewHolder(view), View.OnClickListener {
    private var address: String? = null
    private val summaryView: TextView
    private val titleView: TextView
    private val timeView: TextView
    private val imageView: ImageView
    fun setAddress(address: String?) {
        var address = address
        if (address == null || address.isEmpty()) {
            address = "Unknown Sender"
        }
        this.address = address
        val contact: Contact? = ContactUtils.getContact(address, true)
        titleView.text = contact?.displayName
        if (contact != null) {
            setAvatar(contact)
        }
    }

    fun setSummary(smsBody: Spanned?) {
        summaryView.text = smsBody
    }

    fun setTime(x: Long) {
        val time = TimeUtils.getPrettyElapsedTime(x)
        timeView.text = time
    }

    override fun onClick(view: View) {
        val i = Intent(mContext, CompleteSmsActivity::class.java)
        i.putExtra(view.resources.getString(R.string.address_id), address)
        mContext.startActivity(i)
    }

    private fun setAvatar(contact: Contact) {
        val drawable: Drawable = contact.getAvatar(mContext)
        imageView.setImageDrawable(drawable)
        imageView.setOnClickListener(onImageClick(contact))
    }

    private fun onImageClick(contact: Contact): View.OnClickListener {
        return View.OnClickListener { v ->
            if (Contact.Source.PHONE == contact.source) {
                val phoneContact: PhoneContact = contact as PhoneContact
                ContactsContract.QuickContact.showQuickContact(mContext, view,
                        phoneContact.uri,
                        ContactsContract.QuickContact.MODE_LARGE, null)
            } else {
                val intent = Intent(ContactsContract.Intents.SHOW_OR_CREATE_CONTACT,
                        Uri.fromParts("tel", address, null))
                v.context.startActivity(intent)
            }
        }
    }

    companion object {
        private val TAG = SearchSmsViewHolder::class.java.simpleName
    }

    init {
        titleView = view.findViewById<View>(R.id.sms_title) as TextView
        summaryView = view.findViewById<View>(R.id.sms_summary) as TextView
        timeView = view.findViewById<View>(R.id.sms_time) as TextView
        imageView = view.findViewById<View>(R.id.avatar) as ImageView
        view.setOnClickListener(this)
    }
}