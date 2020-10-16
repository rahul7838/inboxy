package `in`.smslite.extension

import android.app.Activity
import android.app.PendingIntent
import android.content.Intent

fun Activity.getBroadcastPendingIntent(requestId: Int, intent: Intent, flag: Int): PendingIntent {
    return PendingIntent.getBroadcast(
            this,
            requestId,
            intent,
            flag
    )
}