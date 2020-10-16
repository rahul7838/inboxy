package `in`.smslite.viewModel

import `in`.smslite.db.Message
import android.app.Application
import androidx.lifecycle.LiveData

class SearchViewModel(application: Application) : BaseViewModel(application) {

    fun searchMessage(keyword: String?): LiveData<List<Message>> {
        return messageRepository.searchMsg(keyword)
    }
}