package `in`.smslite.viewModel

import `in`.smslite.extension.toMessage
import `in`.smslite.utils.ContactUtils
import android.app.Application
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import kotlinx.coroutines.launch
import me.everything.providers.android.telephony.Sms
import me.everything.providers.android.telephony.TelephonyProvider

class WelcomeViewModel(application: Application) : BaseViewModel(application) {

    private val _completeEvent = MutableLiveData<Boolean>()
    val completeEvent: LiveData<Boolean> = _completeEvent

    fun categoriseSms() {
        ioScope.launch {
            val smses = getAllMessages()
            val size = smses.size
            for (i in 0 until size) {
                val sms = smses[i]
                val contact = ContactUtils.getContact(sms.address, true)
                val message = sms.toMessage(sms, contact?.category)
                messageRepository.insertMessage(message)
            }
            _completeEvent.postValue(true)
        }
    }

    private fun getAllMessages(): List<Sms> {
        val messageProvider = TelephonyProvider(getApplication())
        return messageProvider.getSms(TelephonyProvider.Filter.ALL).list
    }
}