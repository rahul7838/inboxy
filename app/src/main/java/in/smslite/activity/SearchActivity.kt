package `in`.smslite.activity

import `in`.smslite.R
import `in`.smslite.adapter.SearchAdapter
import `in`.smslite.db.Message
import `in`.smslite.viewModel.SearchViewModel
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import org.koin.android.ext.android.inject

/**
 * Created by rahul1993 on 4/21/2018.
 */
class SearchActivity : AppCompatActivity() {
    var searchAdapter: SearchAdapter? = null
    var editText: EditText? = null
    var recyclerView: RecyclerView? = null
    var backArrow: ImageView? = null
    var textView: TextView? = null

    private val viewModel: SearchViewModel by inject()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.search_msg)
        editText = findViewById<View>(R.id.search_editText_id) as EditText
        textView = findViewById(R.id.search_msg_note)
        backArrow = findViewById<View>(R.id.search_activity_back_arrow) as ImageView
        recyclerView = findViewById<View>(R.id.search_result_recycler_view) as RecyclerView
        onBackArrowClick()
        searchTextListener()
        setRecycler()
    }

    private fun setRecycler() {
        val llm = LinearLayoutManager(this)
        llm.orientation = LinearLayoutManager.VERTICAL
        recyclerView!!.setHasFixedSize(true)
        recyclerView!!.layoutManager = llm
        searchAdapter = SearchAdapter(arrayListOf(), "")
        recyclerView!!.adapter = searchAdapter
    }

    private fun searchTextListener() {
        editText?.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {}
            override fun afterTextChanged(s: Editable) {
                val searchKeyword = s.toString()
                viewModel.searchMessage("%$searchKeyword%").observe(this@SearchActivity, {
                    if (it.isNotEmpty()) {
                        searchAdapter?.swapData(it as ArrayList<Message>, searchKeyword)
                        textView?.visibility = View.GONE
                        recyclerView?.visibility = View.VISIBLE
                    } else {
                        searchAdapter?.swapData(arrayListOf(), searchKeyword)
                        textView?.visibility = View.VISIBLE
                        recyclerView?.visibility = View.GONE
                    }
                })
            }
        })
    }

    private fun onBackArrowClick() {
        backArrow?.setOnClickListener { v: View? -> onBackPressed() }
    }
}