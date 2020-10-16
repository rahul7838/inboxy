package `in`.smslite.extension

import `in`.smslite.db.Message
import me.everything.providers.android.telephony.Sms

fun Sms.toMessage(sms: Sms, category: Int?): Message {
    return Message(
            address = sms.address,
            body = sms.body,
            receivedDate = sms.receivedDate,
            sentDate = sms.sentDate,
            errorCode = sms.errorCode,
            locked = sms.locked,
            person = sms.person,
            protocol = sms.protocol,
            read = sms.read,
            seen = sms.seen,
            serviceCenter = sms.serviceCenter,
            status = sms.status.getStatus(),
            subject = sms.subject,
            threadId = sms.threadId,
            type = sms.type.getType(),
            category = category ?: 1,
            )
}

fun Sms.MessageStatus.getStatus(): Message.MessageStatus {
    return when (this) {
        Sms.MessageStatus.COMPLETE -> Message.MessageStatus.COMPLETE
        Sms.MessageStatus.FAILED -> Message.MessageStatus.FAILED
        Sms.MessageStatus.PENDING -> Message.MessageStatus.PENDING
        Sms.MessageStatus.NONE -> Message.MessageStatus.NONE
    }
}

fun Sms.MessageType.getType(): Message.MessageType {
    return when (this) {
        Sms.MessageType.ALL -> Message.MessageType.ALL
        Sms.MessageType.DRAFT -> Message.MessageType.DRAFT
        Sms.MessageType.FAILED -> Message.MessageType.FAILED
        Sms.MessageType.QUEUED -> Message.MessageType.QUEUED
        Sms.MessageType.SENT -> Message.MessageType.SENT
        Sms.MessageType.INBOX -> Message.MessageType.INBOX
        Sms.MessageType.OUTBOX -> Message.MessageType.OUTBOX
    }
}
