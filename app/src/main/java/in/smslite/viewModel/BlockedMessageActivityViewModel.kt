package `in`.smslite.viewModel

import `in`.smslite.db.Message
import android.app.Application
import androidx.lifecycle.LiveData
import kotlinx.coroutines.launch

/**
 * Created by rahul1993 on 5/31/2018.
 */
class BlockedMessageActivityViewModel(application: Application) : BaseViewModel(application) {
    fun updateCategoryOfMessage(address: String?, category: Int, presentCategory: Int) {
        ioScope.launch {
            messageRepository.moveToCategory(address, category, presentCategory)
        }
    }

    val blockedMessage: LiveData<List<Message>>
        get() = messageRepository.getBlockedMessage()
}