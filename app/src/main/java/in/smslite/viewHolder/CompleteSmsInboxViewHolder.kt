package `in`.smslite.viewHolder

import `in`.smslite.R
import `in`.smslite.utils.TimeUtils
import android.view.View
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class CompleteSmsInboxViewHolder(val view: View) : RecyclerView.ViewHolder(view) {
    private val bodyView: TextView = itemView.findViewById<View>(R.id.body) as TextView
    private val timestampView: TextView = itemView.findViewById<View>(R.id.timestamp) as TextView

    fun setCompleteMsg(body: String?) {
        bodyView.text = body
    }

    fun setTime(x: Long) {
        val prettyTime = TimeUtils.getPrettyElapsedTime(x)
        timestampView.text = prettyTime
    }

    fun setBackgroundColorWhite() {
        itemView.setBackgroundColor(view.context.resources.getColor(R.color.white_pure))
    }

    fun setBackgroundColor() {
        itemView.setBackgroundColor(view.context.resources.getColor(R.color.item_selected))
    }
}