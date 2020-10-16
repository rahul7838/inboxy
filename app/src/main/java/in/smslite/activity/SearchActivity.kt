package `in`.smslite.activity

import `in`.smslite.R
import `in`.smslite.adapter.SearchAdapter
import `in`.smslite.db.Message
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.View
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import java.util.*

/**
 * Created by rahul1993 on 4/21/2018.
 */
class SearchActivity : AppCompatActivity() {
    var searchAdapter: SearchAdapter? = null
    var editText: EditText? = null
    var msgList: List<Message> = ArrayList()
    var searchKeyword: String? = null
    var recyclerView: RecyclerView? = null
    var backArrow: ImageView? = null
    var textView: TextView? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.search_msg)
        editText = findViewById<View>(R.id.search_editText_id) as EditText
        textView = findViewById(R.id.search_msg_note)
        backArrow = findViewById<View>(R.id.search_activity_back_arrow) as ImageView
        onBackArrowClick()
        searchTextListener()
        recyclerView = findViewById<View>(R.id.search_result_recycler_view) as RecyclerView
        val llm = LinearLayoutManager(this)
        llm.orientation = LinearLayoutManager.VERTICAL
        recyclerView!!.setHasFixedSize(true)
        recyclerView!!.layoutManager = llm
        searchAdapter = SearchAdapter(msgList, searchKeyword)
        recyclerView!!.adapter = searchAdapter
    }

    private fun searchTextListener() {
        editText!!.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
                Log.d(TAG, "onTextChanged")
            }

            override fun afterTextChanged(s: Editable) {
                Log.d(TAG, "afterTextChanged")
                searchKeyword = s.toString()
                msgList = MainActivity.db?.messageDao()?.searchMsg("%$searchKeyword%") as List<Message>
                Log.d(TAG, searchKeyword!!)
                Log.d(TAG, msgList.size.toString())
                if (searchKeyword != "") {
                    searchAdapter?.swapData(msgList, searchKeyword)
                    textView!!.visibility = View.GONE
                    recyclerView!!.visibility = View.VISIBLE
                } else {
                    searchAdapter?.swapData(ArrayList<Message>(), searchKeyword)
                    textView!!.visibility = View.VISIBLE
                    recyclerView!!.visibility = View.GONE
                }
            }
        })
    }

    private fun onBackArrowClick() {
        backArrow!!.setOnClickListener { v: View? -> onBackPressed() }
    }

    companion object {
        private val TAG = SearchActivity::class.java.simpleName
    }
}