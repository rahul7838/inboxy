package `in`.smslite.viewModel

import `in`.smslite.db.Message
import `in`.smslite.db.MessageDatabase
import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData

/**
 * Created by rahul1993 on 5/31/2018.
 */
class BlockedMessageActivityViewModel(application: Application) : AndroidViewModel(application) {
    private val mDB: MessageDatabase
    fun updateCategoryOfMessage(address: String?, category: Int, presentCategory: Int) {
        mDB.messageDao().moveToCategory(address, category, presentCategory)
    }

    val blockedMessage: LiveData<List<Message>>
        get() = mDB.messageDao().blockedMessage

    init {
        mDB = MessageDatabase.getInMemoryDatabase(application)
    }
}