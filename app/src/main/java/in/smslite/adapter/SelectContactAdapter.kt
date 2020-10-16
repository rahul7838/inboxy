package `in`.smslite.adapter

import `in`.smslite.R
import `in`.smslite.activity.SelectContactActivity
import `in`.smslite.viewHolder.SelectContactViewHolder
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView

/**
 * Created by rahul1993 on 5/27/2018.
 */
class SelectContactAdapter(val list: ArrayList<String?>?, val phoneNumberList: ArrayList<String?>?) : RecyclerView.Adapter<SelectContactViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SelectContactViewHolder {
        val view: View = LayoutInflater.from(parent.context).inflate(R.layout.item_view_select_contact, parent, false)
        return SelectContactViewHolder(view, parent.context)
    }

    override fun onBindViewHolder(holder: SelectContactViewHolder, position: Int) {
        holder.setContactName(list?.get(position))
        holder.setPhoneNumber(phoneNumberList?.get(position))
    }

    override fun getItemCount(): Int {
        return list?.size!!
    }

    fun updateList(nameList: ArrayList<String?>?, numberList: ArrayList<String?>?) {
        list?.clear()
        phoneNumberList?.clear()
        list?.addAll(nameList!!)
        phoneNumberList?.addAll(numberList!!)
        SelectContactActivity.selectContactAdapter?.notifyDataSetChanged()
    }
}