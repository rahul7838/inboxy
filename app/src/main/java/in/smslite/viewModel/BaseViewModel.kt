package `in`.smslite.viewModel

import `in`.smslite.repository.MessageRepository
import android.app.Application
import androidx.lifecycle.AndroidViewModel
import org.koin.core.KoinComponent
import org.koin.core.inject

abstract class BaseViewModel(application: Application) : AndroidViewModel(application), KoinComponent {

    protected val messageRepository: MessageRepository by inject()

    protected val ioScope: CoroutineScope by inject()

    protected val defaultScope: CoroutineScope by inject()
}