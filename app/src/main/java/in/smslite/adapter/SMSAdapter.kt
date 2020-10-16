package `in`.smslite.adapter

import `in`.smslite.R
import `in`.smslite.db.Message
import `in`.smslite.viewHolder.SMSViewHolder
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import java.util.*

/**
 * Created by rahul1993 on 11/15/2017.
 */
class SMSAdapter(var messages: List<Message>, selectedItemAdapter: List<Message>, listOfItemAdapter: List<Message>) : RecyclerView.Adapter<SMSViewHolder>() {
    var selectedItemAdapter: List<Message> = ArrayList()
    var listOfItemAdapter: List<Message> = ArrayList()
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SMSViewHolder {
        return if (viewType == READ) {
            val viewHolder: View = LayoutInflater.from(parent.context).inflate(R.layout.card_read_sms, parent, false)
            SMSViewHolder(viewHolder, parent.context)
        } else {
            val viewHolder: View = LayoutInflater.from(parent.context).inflate(R.layout.card_unread_sms, parent, false)
            SMSViewHolder(viewHolder, parent.context)
        }
    }

    override fun onBindViewHolder(holder: SMSViewHolder, position: Int) {
        if (selectedItemAdapter.contains(listOfItemAdapter[position])) {
            holder.setBackgroundColor()
            holder.setDividerLineInvisible()
        } else {
            holder.setBackgroundColorWhite()
            holder.setDividerLineVisible()
        }
        holder.setAddress(messages[position].getAddress())
        holder.setSummary(messages[position].getBody())
        holder.setTime(messages[position].getTimestamp())
    }

    override fun getItemCount(): Int {
        return messages.size
    }

    override fun getItemViewType(position: Int): Int {
        return if (messages[position].read) READ else UNREAD
    }

    fun setMessage(messages: List<Message>) {
        this.messages = messages
        notifyDataSetChanged()
    }

    companion object {
        private const val READ = 0
        private const val UNREAD = 1
    }

    init {
        this.listOfItemAdapter = listOfItemAdapter
        this.selectedItemAdapter = selectedItemAdapter
    }
}