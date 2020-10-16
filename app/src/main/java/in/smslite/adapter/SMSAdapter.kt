package `in`.smslite.adapter

import `in`.smslite.R
import `in`.smslite.db.Message
import `in`.smslite.viewHolder.SMSViewHolder
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView

/**
 * Created by rahul1993 on 11/15/2017.
 */
class SMSAdapter(var selectedItemAdapter: List<Message>, var listOfItemAdapter: List<Message>) : RecyclerView.Adapter<SMSViewHolder>() {

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
        holder.setAddress(listOfItemAdapter[position].getAddress())
        holder.setSummary(listOfItemAdapter[position].getBody())
        holder.setTime(listOfItemAdapter[position].getTimestamp())
    }

    override fun getItemCount(): Int {
        return listOfItemAdapter.size
    }

    override fun getItemViewType(position: Int): Int {
        return if (listOfItemAdapter[position].read) READ else UNREAD
    }

    fun setMessage(messages: List<Message>) {
        listOfItemAdapter = messages
        notifyDataSetChanged()
    }

    companion object {
        private const val READ = 0
        private const val UNREAD = 1
    }
}