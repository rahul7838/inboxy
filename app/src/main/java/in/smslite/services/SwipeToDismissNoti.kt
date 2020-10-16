package `in`.smslite.services

import `in`.smslite.repository.MessageRepository
import android.app.IntentService
import android.content.Intent
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import org.koin.android.ext.android.inject

/**
 * Created by rahul1993 on 3/18/2018.
 *
 * Creates an IntentService.  Invoked by your subclass's constructor.
 *
 * @param name Used to name the worker thread, important only for debugging.
 */
class SwipeToDismissNoti : IntentService("SwipeToDismissNoti") {

    private val messageRepository: MessageRepository by inject()
    private val ioScope: CoroutineScope by inject()

    override fun onHandleIntent(intent: Intent?) {
        val bundle = intent!!.extras
        val category = bundle!!.getInt(SWIPE_TO_DISMISS_CATEGORY_KEY)
        ioScope.launch { messageRepository.markAllSeen(category) }
    }

    companion object {
        private val TAG = SwipeToDismissNoti::class.java.simpleName
        const val SWIPE_TO_DISMISS_CATEGORY_KEY = "category"
    }
}