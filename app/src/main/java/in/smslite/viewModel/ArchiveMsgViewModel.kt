package `in`.smslite.viewModel

import `in`.smslite.db.Message
import android.app.Application
import androidx.lifecycle.LiveData

/**
 * Created by rahul1993 on 6/1/2018.
 */
class ArchiveMsgViewModel(application: Application) : BaseViewModel(application) {
    val archiveMessage: LiveData<List<Message>>
        get() = messageRepository.getArchiveMessage()
}