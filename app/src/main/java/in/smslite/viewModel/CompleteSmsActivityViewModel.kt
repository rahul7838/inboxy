package `in`.smslite.viewModel

import `in`.smslite.db.Message
import `in`.smslite.utils.ContactUtils
import android.app.Application
import android.content.Intent
import android.provider.ContactsContract
import androidx.lifecycle.LiveData
import org.koin.core.component.KoinApiExtension
import java.util.*

/**
 * Created by rahul1993 on 11/15/2017.
 */
@KoinApiExtension
class CompleteSmsActivityViewModel(application: Application) : BaseViewModel(application) {
    private val isDualSim = false

    fun getMessageListByAddress(address: String?, category: Int): LiveData<List<Message>> {
        return messageRepository.getMessageListByAddress(address, category)
    }

    fun markAllRead(address: String?) {
        messageRepository.markAllReadByAddress(address)
    }

    fun deleteFailedMsg(time: Long?) {
        messageRepository.deleteFailedMsg(time)
    }

    fun findFutureCategory(address: String?): Int {
        return messageRepository.findCategory(address)
    }

    fun queryDataToFindConatact(data: Intent): List<String> {
        val list: MutableList<String> = ArrayList()
        val uri = data.data
        val projection = arrayOf(ContactsContract.CommonDataKinds.Phone.NUMBER, ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME)
        try {
            getApplication<Application>().contentResolver.query(uri!!, projection,
                    null, null, null).use { cursor ->
                val numberIndex = cursor!!.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER)
                val nameIndex = cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME)
                cursor.moveToFirst()
                var number = cursor.getString(numberIndex)
                val name = cursor.getString(nameIndex)
                number = ContactUtils.formatAddress(number)
                list.add(name)
                list.add(number)
            }
        } catch (e: NullPointerException) {
            e.printStackTrace()
        }
        //    Log.i(TAG, number);
        return list
    }
}