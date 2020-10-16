package `in`.smslite.adapter

import `in`.smslite.R
import `in`.smslite.db.Message
import `in`.smslite.viewHolder.SearchSmsViewHolder
import android.graphics.Color
import android.text.Html
import android.text.Spannable
import android.text.SpannableStringBuilder
import android.text.style.ForegroundColorSpan
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import java.util.*

/**
 * Created by rahul1993 on 4/22/2018.
 */
class SearchAdapter(list: List<Message>, searchKeyword: String?) : RecyclerView.Adapter<SearchSmsViewHolder>() {
    private var list: ArrayList<Message>? = ArrayList()
    private var searchKeyword: String?
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SearchSmsViewHolder {
        val view: View = LayoutInflater.from(parent.context).inflate(R.layout.search_sms_item, parent, false)
        return SearchSmsViewHolder(view, parent.context)
    }

    override fun onBindViewHolder(holder: SearchSmsViewHolder, position: Int) {
        val address = list!![position].getAddress()
        val summary = list!![position].getBody()
        holder.setTime(list!![position].getTimestamp())
        holder.setAddress(address)
        val sb: SpannableStringBuilder
        if (searchKeyword != null) {
            if (searchKeyword!!.length > 0) {
                //color your text here
                val index = summary.toLowerCase().indexOf(searchKeyword!!.toLowerCase())
                if (index >= 0) {
                    sb = SpannableStringBuilder(summary)
                    val fcs = ForegroundColorSpan(Color.rgb(0, 0, 255)) //specify color here
                    sb.setSpan(fcs, index, index + searchKeyword!!.length, Spannable.SPAN_INCLUSIVE_INCLUSIVE)
                    holder.setSummary(sb)
                } else {
                    holder.setSummary(Html.fromHtml(summary))
                }
            } else {
                holder.setSummary(Html.fromHtml(summary))
            }
        }
    }

    override fun getItemCount(): Int {
        return list?.size ?: 0
    }

    fun swapData(data: List<Message>?, searchKeyword: String?) {
        if (data == null) return
        if (list != null) {
            list!!.clear()
            list!!.addAll(data)
            notifyDataSetChanged()
        }
        this.searchKeyword = searchKeyword
    }

    init {
        this.list = list as ArrayList<Message>
        this.searchKeyword = searchKeyword
    }
}