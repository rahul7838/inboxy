package `in`.smslite.viewHolder

import `in`.smslite.R
import `in`.smslite.activity.CompleteSmsActivity
import `in`.smslite.drawable.DataSource
import `in`.smslite.utils.ContactUtils
import android.content.Context
import android.content.Intent
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

/**
 * Created by rahul1993 on 5/27/2018.
 */
class SelectContactViewHolder(private val view: View, context: Context) : RecyclerView.ViewHolder(view), View.OnClickListener {
    private var address: String? = null
    private val contactName: TextView
    private val phoneNumber: TextView
    private val avatar: ImageView
    private val context: Context
    fun setContactName(name: String?) {
        contactName.text = name
        setAvatar(name)
    }

    fun setPhoneNumber(number: String?) {
        address = number
        phoneNumber.text = number
    }

    fun setAvatar(displayName: String?) {
        val drawable = DataSource.getInstance(context).getDrawable(displayName)
        //    Bitmap b = in.smslite.utils.DrawableUtils.getBitmap(drawable);
//
//  Drawable drawable1 = RoundedBitmapDrawableFactory.create(context.getResources(), b);
        avatar.setImageDrawable(drawable)
    }

    override fun onClick(v: View) {
        val intent = Intent(context, CompleteSmsActivity::class.java)
        intent.putExtra(context.resources.getString(R.string.address_id), ContactUtils.normalizeNumber(address))
        context.startActivity(intent)
    }

    init {
        contactName = view.findViewById(R.id.textview1_select_contact_id)
        phoneNumber = view.findViewById(R.id.textView2_select_contact_id)
        avatar = view.findViewById(R.id.imageView_select_contact_id)
        this.context = context
        view.setOnClickListener(this)
    }
}