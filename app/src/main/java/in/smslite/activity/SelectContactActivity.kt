package `in`.smslite.activity

import `in`.smslite.R
import `in`.smslite.adapter.SelectContactAdapter
import `in`.smslite.databinding.ActivitySelectContact2Binding
import `in`.smslite.utils.ContactUtils
import android.content.Context
import android.database.Cursor
import android.os.Bundle
import android.provider.ContactsContract
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.loader.app.LoaderManager
import androidx.loader.content.CursorLoader
import androidx.loader.content.Loader
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.Recycler
import java.util.*
import kotlin.collections.ArrayList

/**
 * Created by rahul1993 on 4/27/2018.
 */
class SelectContactActivity : AppCompatActivity(), LoaderManager.LoaderCallbacks<Cursor> {
    private var context: Context? = null
    var list: ArrayList<String?>? = ArrayList()
    var phoneNumberList: ArrayList<String?>? = ArrayList()
    private lateinit var selectContactAdapter: SelectContactAdapter

    // Defines a variable for the search string
    private var mSearchString: String? = null

    // Defines the array to hold values that replace the ?
    var callbacks: LoaderManager.LoaderCallbacks<*>? = null
    private var binding: ActivitySelectContact2Binding? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        context = this
        callbacks = this
        binding = ActivitySelectContact2Binding.inflate(layoutInflater)
        setContentView(R.layout.activity_select_contact2)
        binding!!.selectContactActivityBackArrow.setOnClickListener { v: View? -> onBackPressed() }
        val linearLayoutManager = LinearLayoutManager(this)
        binding!!.editTextSelectListId.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {}
            override fun afterTextChanged(s: Editable) {
                val text = StringBuilder()
                text.append(s)
                mSearchString = text.toString()
                supportLoaderManager.restartLoader<Any>(0, null, callbacks as LoaderManager.LoaderCallbacks<Any>)
                //        getLoaderManager().initLoader(0,null, callbacks);
                Log.d(TAG, "afterTextChsnged")
            }
        })
        binding!!.recyclerViewSelectListId.setHasFixedSize(true)
        binding!!.recyclerViewSelectListId.layoutManager = linearLayoutManager
        //    recyclerView.setLayoutManager(new WrapContentLinearLayoutManager(context));
        selectContactAdapter = SelectContactAdapter(list, phoneNumberList)
        binding!!.recyclerViewSelectListId.adapter = selectContactAdapter
    }

    override fun onCreateLoader(id: Int, args: Bundle?): Loader<Cursor> {
        phoneNumberList?.clear()
        list?.clear()
        return if (!mSearchString!!.matches(Regex("[0-9]*"))) {
            Log.d(TAG, "$mSearchString string")
            val projection = arrayOf(
                    ContactsContract.Data._ID,
                    ContactsContract.Contacts.HAS_PHONE_NUMBER,
                    ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME_PRIMARY,
                    ContactsContract.CommonDataKinds.Phone.NUMBER
            )
            val selection = ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME_PRIMARY + " LIKE ? " +  //          + " OR "+ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME_PRIMARY + " LIKE ?" +
                    " AND " + ContactsContract.Data.MIMETYPE + " LIKE ?"
            val selectionCriteria = arrayOf("%$mSearchString%",
                    ContactsContract.CommonDataKinds.Phone.CONTENT_ITEM_TYPE)
            val cursor = CursorLoader(this, ContactsContract.Data.CONTENT_URI, projection, selection,
                    selectionCriteria, ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME_PRIMARY + " ASC" + " LIMIT 200 ")
            Log.d(TAG, "onCreateLoader")
            cursor
        } else {
            val uri = ContactsContract.CommonDataKinds.Phone.CONTENT_URI
            val selection = (ContactsContract.CommonDataKinds.Phone.NUMBER + " LIKE?" + " OR "
                    + ContactsContract.CommonDataKinds.Phone.NUMBER + " LIKE?" + " OR "
                    + ContactsContract.CommonDataKinds.Phone.NUMBER + " LIKE?" + " OR "
                    + ContactsContract.CommonDataKinds.Phone.NUMBER + " LIKE?")
            //      String[] arg = {"_" + mSearchString + "%" + " OR " + "___" + mSearchString + "%" + " OR " + mSearchString + "%"};
            val length = mSearchString!!.length
            var newSearchString: String? = null
            val builder = StringBuilder()
            for (i in 0 until length) {
                newSearchString = mSearchString!!.substring(i, i + 1)
                builder.append(newSearchString)
                if (builder.length == 2 || builder.length == 5 || builder.length == 8 || builder.length == 11) {
                    builder.append("%")
                }
            }
            Log.d(TAG, builder.toString())
            val arg = arrayOf("+91$builder%", "+91 $builder%", "$builder%", "0$builder%")
            CursorLoader(context!!, uri, null, selection, arg, ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME_PRIMARY + " ASC" + " LIMIT 200 ")
        }
    }

    override fun onLoadFinished(loader: Loader<Cursor>, data: Cursor) {
        if (mSearchString == "") {
            selectContactAdapter.updateList(list, phoneNumberList)
        } else {
            Log.d(TAG, Integer.toString(data.count))
            if (!mSearchString!!.matches(Regex("[0-9]*"))) {
                textIsAlphaNumeric(data)
            } else {
                textIsNumber(data)
            }
        }
    }

    private fun textIsNumber(cursor: Cursor) {
//    String number = null;
        val nameList: ArrayList<String?> = ArrayList()
        val numberList: ArrayList<String?> = ArrayList()
        val set = HashSet<String>()
        numberList.add(mSearchString)
        nameList.add(mSearchString)
        try {
            cursor.moveToFirst()
            val nameIndex = cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME)
            val numberIndex = cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER)
            do {
                val formatedNumber: String = ContactUtils.normalizeNumber(cursor.getString(numberIndex))
                //        if (set.add(formatNumber(cursor.getString(numberIndex)))) {
                if (set.add(formatedNumber)) {
                    nameList.add(cursor.getString(nameIndex))
                    numberList.add(formatedNumber)
                }
            } while (cursor.moveToNext())
        } catch (e: Exception) {
            e.printStackTrace()
        } finally {
            cursor.close()
        }
        selectContactAdapter.updateList(nameList, numberList)
    }

    private fun textIsAlphaNumeric(cursor: Cursor) {
        val nameList: ArrayList<String?>? = ArrayList()
        val numberList: ArrayList<String?>? = ArrayList()
        val set = HashSet<String>()
        cursor.moveToFirst()
        try {
            val nameIndex = cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME)
            val numberIndex = cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER)
            do {
//          if (cursor.getInt(1) == 1) {
                if (!cursor.getString(numberIndex).matches(Regex(".*[a-zA-Z].*"))) {
                    val formatedNumber: String = ContactUtils.normalizeNumber(cursor.getString(numberIndex))
                    if (set.add(formatedNumber)) {
                        nameList?.add(cursor.getString(nameIndex))
                        numberList?.add(formatedNumber)
                    }
                }
            } while (cursor.moveToNext())
        } catch (e: Exception) {
            e.printStackTrace()
        } finally {
            cursor.close()
        }
//        Log.d(TAG, Integer.toString(nameList?.size))
//        Log.d(TAG, Integer.toString(numberList?.size))
        selectContactAdapter.updateList(nameList, numberList)
    }

    override fun onLoaderReset(loader: Loader<Cursor>) {}
    private fun formatNumber(number: String): String {
        var number = number
        number = number.replace("-".toRegex(), "")
        number = number.replace(" ".toRegex(), "")
        if (number[0] == '+') {
            number = number.substring(3)
            return number
        }
        if (number[0] == '0') {
            number = number.substring(1)
            return number
        }
        return number
    }

    inner class WrapContentLinearLayoutManager(context: Context?) : LinearLayoutManager(context) {
        override fun onLayoutChildren(recycler: Recycler, state: RecyclerView.State) {
            try {
                super.onLayoutChildren(recycler, state)
            } catch (e: IndexOutOfBoundsException) {
                Log.e("probe", "meet a IOOBE in recycler view")
            }
        }
    }

    companion object {
        private val TAG = SelectContactActivity::class.java.simpleName

        @JvmField
        var selectContactAdapter: SelectContactAdapter? = null
    }
}