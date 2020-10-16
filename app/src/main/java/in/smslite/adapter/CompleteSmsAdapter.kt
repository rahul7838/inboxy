package `in`.smslite.adapter

import `in`.smslite.R
import `in`.smslite.db.Message
import `in`.smslite.viewHolder.CompleteSmsInboxViewHolder
import `in`.smslite.viewHolder.CompleteSmsSentViewHolder
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import java.util.*

class CompleteSmsAdapter(var smsConversation: List<Message>, address: String, sendTextSms: SendTextSms, selectedItemAdapter: List<Message>, listOfItemAdapter: List<Message>) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    private val INBOX = 0
    private val SENT = 1
    private val FAILED = 2
    private val QUEUED = 3
    private val OUTBOX = 4
    var selectedItemAdapter: List<Message> = ArrayList()
    var listOfItemAdapter: List<Message> = ArrayList()
    private val address: String
    private val sendTextSms: SendTextSms
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return if (viewType == INBOX) {
            val view: View = LayoutInflater.from(parent.context).inflate(R.layout.card_inbox, parent, false)
            CompleteSmsInboxViewHolder(view)
        } else if (viewType == SENT) {
            val view: View = LayoutInflater.from(parent.context).inflate(R.layout.card_sent, parent, false)
            CompleteSmsSentViewHolder(view, address, sendTextSms)
        } else if (viewType == FAILED) {
            val view: View = LayoutInflater.from(parent.context).inflate(R.layout.card_sent_failed_sms, parent, false)
            CompleteSmsSentViewHolder(view, address, sendTextSms)
        } else if (viewType == QUEUED) {
            val view: View = LayoutInflater.from(parent.context).inflate(R.layout.card_sending_sms, parent, false)
            CompleteSmsSentViewHolder(view, address, sendTextSms)
        } else {
            val view: View = LayoutInflater.from(parent.context).inflate(R.layout.card_delivered_sms, parent, false)
            CompleteSmsSentViewHolder(view, address, sendTextSms)
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        if (holder is CompleteSmsInboxViewHolder) {
            val completeSmsViewHolder: CompleteSmsInboxViewHolder = holder as CompleteSmsInboxViewHolder
            if (selectedItemAdapter.contains(listOfItemAdapter[position])) {
                completeSmsViewHolder.setBackgroundColor()
            } else {
                completeSmsViewHolder.setBackgroundColorWhite()
            }
            completeSmsViewHolder.setCompleteMsg(smsConversation[position].body)
            completeSmsViewHolder.setTime(smsConversation[position].timestamp)
        } else {
            val completeSmsSentViewHolder: CompleteSmsSentViewHolder = holder as CompleteSmsSentViewHolder
            if (selectedItemAdapter.contains(listOfItemAdapter[position])) {
                completeSmsSentViewHolder.setBackgroundColor()
                completeSmsSentViewHolder.setItemBackgroundBlack()
            } else {
                completeSmsSentViewHolder.setBackgroundColorWhite()
                completeSmsSentViewHolder.setItemBackgroundBlue()
            }
            completeSmsSentViewHolder.setCompleteMsg(smsConversation[position].body)
            completeSmsSentViewHolder.setTime(smsConversation[position].timestamp)
        }
    }

    fun getColor(holder: RecyclerView.ViewHolder?, position: Int) {}
    override fun getItemCount(): Int {
        return smsConversation.size
    }

    override fun getItemViewType(position: Int): Int {
        if (Message.MessageType.INBOX == smsConversation[position].type) {
            return INBOX
        } else if (Message.MessageType.FAILED == smsConversation[position].type) {
            return FAILED
        } else if (Message.MessageType.QUEUED == smsConversation[position].type) {
            return QUEUED
        } else if (Message.MessageType.OUTBOX == smsConversation[position].type) {
            return OUTBOX
        }
        return SENT
    }

    fun setMessage(message: List<Message>) {
        smsConversation = message
        notifyDataSetChanged()
    }

    interface SendTextSms {
        fun onSendTextSms(time: Long?, address: String?, category: Int)
    }

    companion object {
        private val TAG = CompleteSmsAdapter::class.java.simpleName
    }

    init {
        this.selectedItemAdapter = selectedItemAdapter
        this.listOfItemAdapter = listOfItemAdapter
        this.address = address
        this.sendTextSms = sendTextSms
    }
}