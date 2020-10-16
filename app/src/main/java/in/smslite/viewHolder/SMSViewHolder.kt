package `in`.smslite.viewHolder

import `in`.smslite.R
import `in`.smslite.contacts.Contact
import `in`.smslite.contacts.PhoneContact
import `in`.smslite.utils.TimeUtils
import android.content.Context
import android.content.Intent
import android.graphics.drawable.Drawable
import android.net.Uri
import android.provider.ContactsContract
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

/**
 * Created by rahul1993 on 11/15/2017.
 */
class SMSViewHolder(private val view: View, private val mContext: Context) : RecyclerView.ViewHolder(view) {
    private var address: String? = null
    private val summaryView: TextView

    //  private TextView titleView;
    private val timeView: TextView
    private val imageView: ImageView
    private val recyclerView: RecyclerView? = null
    private val dividerLine: View
    var titleView: TextView
    fun setDividerLineVisible() {
        dividerLine.visibility = View.VISIBLE
    }

    fun setDividerLineInvisible() {
        dividerLine.visibility = View.INVISIBLE
    }

    fun setAddress(address: String?) {
        var address = address
        if (address == null || address.isEmpty()) {
            address = "Unknown Sender"
        }
        this.address = address
        val contact: PhoneContact? = PhoneContact.get(address, true)
        //    Contact contact = ContactUtils.getContact(address, mContext, true);
        titleView.text = contact?.displayName
        //    titleView.setText(address);
        contact?.let { setAvatar(it) }
    }

    fun setSummary(smsBody: String?) {
        summaryView.text = smsBody
    }

    fun setTime(x: Long) {
//    Date date = new Date(x);// we have to pass Date in getPrettyElaspsedTime, convert long into Date.
        val time = TimeUtils.getPrettyElapsedTime(x)
        //        Log.i(TAG, time);
        timeView.text = time
    }

    private fun setAvatar(contact: Contact) {
        /* if (Contact.Source.FIREBASE.equals(contact.getSource())) {
      CompanyContact companyContact = (CompanyContact) contact;
      Glide.with(mContext)
              .using(new FirebaseImageLoader())
              .load(FirebaseUtils.getStorageRef().child(companyContact.getUriPhoto()))
              .placeholder(R.drawable.ic_account)
              .error(R.drawable.ic_account)
              .into(imageView);
    } else {*/
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

    fun setBackgroundColorWhite() {
        view.setBackgroundColor(mContext.resources.getColor(R.color.white_pure))
    }

    fun setBackgroundColor() {
        view.setBackgroundColor(mContext.resources.getColor(R.color.item_selected))
    }

    companion object {
        private const val TAG = "SMSViewHolder"
    }

    init {
        titleView = view.findViewById(R.id.sms_title)
        summaryView = view.findViewById<View>(R.id.sms_summary) as TextView
        timeView = view.findViewById<View>(R.id.sms_time) as TextView
        imageView = view.findViewById<View>(R.id.avatar) as ImageView
        dividerLine = view.findViewById(R.id.itemDivider) as View
    }
}