package `in`.smslite.viewHolder

import `in`.smslite.R
import `in`.smslite.adapter.CompleteSmsAdapter
import `in`.smslite.utils.TimeUtils
import android.preference.PreferenceManager
import android.util.Log
import android.view.View
import android.widget.LinearLayout
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class CompleteSmsSentViewHolder(private val view: View, private val address: String, sendTextSmsListener: CompleteSmsAdapter.SendTextSms) : RecyclerView.ViewHolder(view), View.OnClickListener {
    private var failedSmsTime: Long? = null
    private val completeMsg: TextView
    private val timeView: TextView
    private val linearLayout: LinearLayout
    private val smsStatusTextView: TextView
    private val sendTextSmsListener: CompleteSmsAdapter.SendTextSms
    fun setCompleteMsg(body: String?) {
        completeMsg.text = body
    }

    fun setTime(x: Long) {
        failedSmsTime = x
        val timea = TimeUtils.getPrettyElapsedTime(x)
        timeView.text = timea
    }

    override fun onClick(v: View) {
        Log.d(TAG, "trySendFailedSms")
        Log.d(TAG, java.lang.Long.toString(failedSmsTime!!))
        tryFailedSms = true
        val category = PreferenceManager.getDefaultSharedPreferences(v.context).getInt(view.context.getString(R.string.dialog_option), 0)
        sendTextSmsListener.onSendTextSms(failedSmsTime, address, category)
    }

    fun setItemBackgroundBlack() {
        linearLayout.setBackgroundResource(R.drawable.sms_sent_background_black)
    }

    fun setItemBackgroundBlue() {
        linearLayout.setBackgroundResource(R.drawable.sms_sent_background)
    }

    fun setBackgroundColorWhite() {
        view.setBackgroundColor(view.context.resources.getColor(R.color.white_pure))
    }

    fun setBackgroundColor() {
        view.setBackgroundColor(view.context.resources.getColor(R.color.item_selected))
    }

    companion object {
        private val TAG = CompleteSmsSentViewHolder::class.java.simpleName
        var tryFailedSms = false
    }

    init {
        this.sendTextSmsListener = sendTextSmsListener
        linearLayout = itemView.findViewById(R.id.card_sent_linear_layout_id)
        completeMsg = view.findViewById<View>(R.id.body) as TextView
        timeView = view.findViewById<View>(R.id.timestamp) as TextView
        smsStatusTextView = view.findViewById<View>(R.id.user_sms_status_id) as TextView
        smsStatusTextView.setOnClickListener(this)
    }
}