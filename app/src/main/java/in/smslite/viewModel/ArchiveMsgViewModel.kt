package `in`.smslite.viewModel

import `in`.smslite.db.Message
import `in`.smslite.db.MessageDatabase
import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData

/**
 * Created by rahul1993 on 6/1/2018.
 */
class ArchiveMsgViewModel(application: Application) : AndroidViewModel(application) {
    private val mDB: MessageDatabase
    val archiveMessage: LiveData<List<Message>>
        get() = mDB.messageDao().getArchiveMessage()

    init {
        mDB = MessageDatabase.getInMemoryDatabase(application)
    }
}