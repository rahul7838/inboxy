package `in`.smslite.viewModel

import `in`.smslite.db.Message
import `in`.smslite.utils.ContactUtils
import android.app.Application
import android.content.Intent
import android.database.Cursor
import android.provider.ContactsContract
import androidx.lifecycle.LiveData
import kotlinx.coroutines.launch

/**
 * Created by rahul1993 on 11/11/2017.
 */
class LocalMessageDbViewModel(
        application: Application
) : BaseViewModel(application) {

    fun getMessageListByCategory(category: Int?): LiveData<List<Message>> {
        return messageRepository.getMessageListByCategory(category)
    }

    val sentSmsCount: Cursor?
        get() = messageRepository.sentSmsCount

    fun insertMessage(message: Message?) {
        ioScope.launch {
            messageRepository.insertMessage(message)
        }
    }

    fun markAllSeen(category: Int?) {
        ioScope.launch {
            messageRepository.markAllSeen(category)
        }
    }

    fun markAllRead(address: String?) {
        ioScope.launch {
            messageRepository.markAllReadByAddress(address)
        }
    }

    fun deleteSelectedConversation(timeStamp: String?) {
        ioScope.launch {
            messageRepository.deleteSelectedConversation(timeStamp)
        }
    }

    fun pickContactSelected(data: Intent): String {
        val uri = data.data
        val projection = arrayOf(ContactsContract.CommonDataKinds.Phone.NUMBER)
        val cursor = getApplication<Application>().contentResolver.query(uri!!, projection,
                null, null, null)
        cursor!!.moveToFirst()
        val columnIndex = cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER)
        var number = cursor.getString(columnIndex)
        number = ContactUtils.formatAddress(number)
        return number
    }

}