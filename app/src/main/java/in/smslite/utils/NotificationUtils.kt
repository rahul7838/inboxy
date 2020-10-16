package `in`.smslite.utils

import `in`.smslite.R
import `in`.smslite.activity.CompleteSmsActivity
import `in`.smslite.activity.MainActivity
import `in`.smslite.contacts.Contact
import `in`.smslite.db.Message
import `in`.smslite.repository.MessageRepository
import `in`.smslite.services.OTPService
import `in`.smslite.services.SwipeToDismissNoti
import android.annotation.TargetApi
import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.database.Cursor
import android.graphics.Color
import android.media.RingtoneManager
import android.os.Build
import android.os.Bundle
import android.preference.PreferenceManager
import android.util.Log
import android.widget.RemoteViews
import androidx.core.app.NotificationCompat
import androidx.core.app.TaskStackBuilder
import androidx.core.content.ContextCompat
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.async
import kotlinx.coroutines.runBlocking
import org.koin.core.KoinComponent
import org.koin.core.inject
import org.koin.core.qualifier.named
import java.lang.Boolean
import java.text.SimpleDateFormat
import java.util.*
import java.util.regex.Pattern

object NotificationUtils : KoinComponent {
    private var vibrate = true
    private var led = true
    private var sound = true
    private val uri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)
    const val BUNDLE_OTP_KEY = "notification id key"
    const val BROADCAST_SMS_CATEGORY_KEY = "category"
    const val NOTIFICATION_BUNDLE_CATEGORY_KEY = "category"
    private const val GROUP_KEY = "key"
    private val TAG = NotificationUtils::class.java.simpleName
    var notificationBundle = Bundle()

    private val messageRepository: MessageRepository by inject()
    private val ioScope: CoroutineScope by inject(named("IO"))

    @JvmStatic
    fun sendGroupedNotification(context: Context, contact: Contact, message: Message) = runBlocking {
        val displayName = contact.displayName
        val drawable = contact.getAvatar(context)
        val category = contact.category
        val channelId = Integer.toString(category)
        val receivedDate = message.receivedDate.toInt()


//    ArrayList<Integer> listCategoryIntValue = new ArrayList<Integer>(Arrays.asList(Contact.UNCATEGORIZED, Contact.PRIMARY, Contact.FINANCE, Contact.PROMOTIONS, Contact.UPDATES));
//    ArrayList<Integer> listId = new ArrayList<>(Arrays.asList(0, priID, finID, proID, updID));
        val listCategoryStringValue: List<String> = ArrayList(Arrays.asList("0", "Primary", "Finance", "Promotion", "Updates"))
        val cursor: Cursor? = messageRepository.getUnseenSmsCount(contact.category)
        val countUnseenSMS = cursor?.count
//        Log.i(TAG, Integer.toString(countUnseenSMS) + " countUnseenSMS")
        val sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context)
        vibrate = sharedPreferences.getBoolean(context.getString(R.string.pref_key_notification_vibration), true)
        led = sharedPreferences.getBoolean(context.getString(R.string.pref_key_notification_LED), true)
        sound = sharedPreferences.getBoolean(context.getString(R.string.pref_key_notification_sound), true)
        val defValues: MutableSet<String> = HashSet()
        defValues.add("Primary")
        defValues.add("Finance")
        defValues.add("Promotion")
        defValues.add("Updates")
        val set = sharedPreferences.getStringSet(context.getString(R.string.pref_key_category), defValues)
        Log.i(TAG, Integer.toString(set!!.size) + "setSize")
        val categoryStringValue = listCategoryStringValue[category]
        if (Build.VERSION.SDK_INT >= 26) {
            createNotificationChannel(context, channelId, categoryStringValue)
        }

        // swipe to dismiss notification makes seen=1(true)
        val swipeToDismissNotiIntent = Intent(context, SwipeToDismissNoti::class.java)
        swipeToDismissNotiIntent.putExtra(SwipeToDismissNoti.SWIPE_TO_DISMISS_CATEGORY_KEY, category)
        val swipeToDismissNotiPendingIntent = PendingIntent.getService(context, 12, swipeToDismissNotiIntent, PendingIntent.FLAG_UPDATE_CURRENT)
        if (set.contains(categoryStringValue)) {
            val builder = NotificationCompat.Builder(context, channelId)
            builder
                    .setAutoCancel(true)
                    .setCategory(Notification.CATEGORY_MESSAGE)
                    .setColor(ContextCompat.getColor(context, R.color.colorPrimary))
                    .setSmallIcon(R.drawable.ic_stat_name)
                    .setLargeIcon(drawable.bitmap)
                    .setChannelId(channelId)
                    .setDeleteIntent(swipeToDismissNotiPendingIntent)
            if (vibrate) {
                builder.setVibrate(longArrayOf(300, 300, 300, 300))
            }
            if (Build.VERSION.SDK_INT < Build.VERSION_CODES.O) {
                builder.priority = NotificationCompat.PRIORITY_HIGH
            }
            if (led) {
                builder.setLights(Color.WHITE, 300, 1000)
                Log.i(TAG, Boolean.toString(led) + " ledPreferenceValue")
            }
            if (sound) {
                builder.setSound(uri)
            }
            val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

            //Mainactivity Pending Intent
            val taskStackBuilderMainActivity = TaskStackBuilder.create(context)
            val mainActivityintent = Intent(context, MainActivity::class.java)
                    .putExtra(BROADCAST_SMS_CATEGORY_KEY, category)
            taskStackBuilderMainActivity.addNextIntent(mainActivityintent)
            taskStackBuilderMainActivity.addParentStack(MainActivity::class.java)
            //      PendingIntent mainActivityPendingIntent = PendingIntent.getActivity(context, 12, mainActivityintent, PendingIntent.FLAG_UPDATE_CURRENT);
            val mainActivityPendingIntent = taskStackBuilderMainActivity.getPendingIntent(12, PendingIntent.FLAG_UPDATE_CURRENT)

            //Complete Sms Activity Pending Intent
            val taskStackBuilderCompleteSmsActivity = TaskStackBuilder.create(context)
            val completeSmsActivityintent = Intent(context, CompleteSmsActivity::class.java)
                    .putExtra(context.resources.getString(R.string.address_id), contact.number)
            taskStackBuilderCompleteSmsActivity.addNextIntent(mainActivityintent)
            taskStackBuilderCompleteSmsActivity.addNextIntent(completeSmsActivityintent)
            val completeSmsActivityPendingIntent = taskStackBuilderCompleteSmsActivity.getPendingIntent(54, PendingIntent.FLAG_UPDATE_CURRENT)

            // Inbox notification style for api 24 and 25 (Nought)
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                builder.setContentTitle(contact.displayName)
                        .setContentText(message.body)
                        .setContentIntent(completeSmsActivityPendingIntent)
                        .setGroup(categoryStringValue)
//                        .setGroupAlertBehavior(GROUP_ALERT_SUMMARY)
                        .setStyle(NotificationCompat.BigTextStyle()
                                .bigText(message.body))
                val bundleBuilder = NotificationCompat.Builder(context, channelId) //            .setContentText(notificationSummary.get(0).getAddress() + ": " + notificationSummary.get(0).getBody())
                        .setGroupSummary(true)
                        .setChannelId(channelId)
                        .setAutoCancel(true)
                        .setColor(ContextCompat.getColor(context, R.color.colorPrimary))
                        .setGroup(categoryStringValue)
                        .setSmallIcon(R.drawable.ic_stat_name) //            .setSubText(String.valueOf(countUnseenSMS) + " new messages")
                        .setContentIntent(mainActivityPendingIntent)
                //
                Log.d(TAG, "api 25")
                if (notificationManager != null) {
                    PreferenceManager.getDefaultSharedPreferences(context).edit().putInt(context.getString(R.string.dialog_option), category).apply()
                    notificationManager.notify(receivedDate, builder.build())
                    notificationManager.notify(category, bundleBuilder.build())
                }
            } else {
                val notificationSummary: List<Message> = ioScope.async { messageRepository.getNotificationSummary(category) }.await()
                val sizeSummary = notificationSummary.size
                Log.i(TAG, Integer.toString(sizeSummary) + "sizeSummary")
                if (sizeSummary > 1) {
                    Log.d(TAG, "inboxStyleSms")
                    val inboxStyle = NotificationCompat.InboxStyle()
                    var i = 0
                    while (i < notificationSummary.size && i < 7) {
                        inboxStyle.addLine(notificationSummary[i].address + ":" + notificationSummary[i].body)
                        i++
                    }
                    inboxStyle.setBigContentTitle(notificationSummary.size.toString() + " new " + categoryStringValue + " messages")
                    if (notificationSummary.size > 7) {
                        inboxStyle.setSummaryText(Math.abs(notificationSummary.size - 7).toString() + " more messages")
                    }
                    Log.i(TAG, "API 22+23")
                    builder.setContentText(notificationSummary[0].address + ": " + notificationSummary[0].body)
                            .setContentIntent(mainActivityPendingIntent)
                            .setContentTitle("$countUnseenSMS new messages")
                            .setStyle(inboxStyle)
                } else {
                    Log.d(TAG, "singleSmsDisplay")
                    builder.setContentText(message.body)
                            .setContentTitle(displayName)
                            .setStyle(NotificationCompat.BigTextStyle()
                                    .bigText(message.body))
                            .setContentIntent(completeSmsActivityPendingIntent)
                }
                Log.i(TAG, "NotificationExecuted")
                if (notificationManager != null) {
                    PreferenceManager.getDefaultSharedPreferences(context).edit().putInt(context.getString(R.string.dialog_option), category).apply()
                    notificationManager.notify(category, builder.build())
                }
            }
        }
    }

    @TargetApi(26)
    private fun createNotificationChannel(context: Context, channelId: String, channelName: String) {
        val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        if (notificationManager != null && notificationManager.getNotificationChannel(channelId) == null) {
            val notificationChannel = NotificationChannel(channelId, channelName, NotificationManager.IMPORTANCE_HIGH)
            notificationManager.createNotificationChannel(notificationChannel)
        }
    }

    @JvmStatic
    fun sendCustomNotification(context: Context, address: String?, body: String, timeStamp: Long?, contact: Contact) {
        val notiId = System.currentTimeMillis().toInt()
        val channelId = "otp"
        var OTP = getOTPFromString(body)
        val OTPID = OTP
        val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        val date = Date(timeStamp!!)
        val sdf = SimpleDateFormat("h:mm a")
        sdf.timeZone = TimeZone.getDefault()
        val formattedDate = sdf.format(date)
        if (OTP != null) {
            OTP = OTP.replace("".toRegex(), "    ").trim { it <= ' ' }
        }

        // normal view of notification layout
        val notificationLayout = RemoteViews(context.packageName, R.layout.custom_noti)
        notificationLayout.setTextViewText(R.id.custom_noti_title, address)
        notificationLayout.setTextViewText(R.id.custom_noti_text, OTP)

        // Copy button click execute the following pending intent
        val intent = Intent(context, OTPService::class.java)
        intent.putExtra(OTPService.NOTIFICATION_ID, notiId)
        intent.putExtra(BUNDLE_OTP_KEY, OTPID)
        val pendingIntent = PendingIntent.getService(context, 12, intent, PendingIntent.FLAG_UPDATE_CURRENT)
        notificationLayout.setOnClickPendingIntent(R.id.relative_layout_copy, pendingIntent)

// Expanded notification layout
        val bigNotificationLayout = RemoteViews(context.packageName, R.layout.custom_notification_big)
        bigNotificationLayout.setTextViewText(R.id.custom_big_noti_title, address)
        bigNotificationLayout.setTextViewText(R.id.custom_big_noti_OTP, OTP)
        bigNotificationLayout.setOnClickPendingIntent(R.id.layout_big_noti_child2, pendingIntent)
        bigNotificationLayout.setTextViewText(R.id.custom_big_noti_time, formattedDate)

        // Pending intent for notification click
        val sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context)
        val taskStackBuilder = TaskStackBuilder.create(context)
        val intent1 = Intent(context, CompleteSmsActivity::class.java)
                .putExtra(context.resources.getString(R.string.address_id), contact.number)
        val mainactivityIntent = Intent(context, MainActivity::class.java)
        mainactivityIntent.putExtra(BROADCAST_SMS_CATEGORY_KEY, contact.category)
        //        .setAction(Long.toString(System.currentTimeMillis()));
//            .setFlags(Intent.FLAG_ACTIVITY_NEW_TASK|Intent.FLAG_ACTIVITY_CLEAR_TASK);
        taskStackBuilder.addNextIntent(mainactivityIntent)
        taskStackBuilder.addNextIntent(intent1)
        val contentPendingIntent = taskStackBuilder.getPendingIntent(15, PendingIntent.FLAG_UPDATE_CURRENT)
        if (Build.VERSION.SDK_INT >= 26) {
            createNotificationChannel(context, channelId, "OTP")
        }

        // Custom notification builder
        val customNotification = NotificationCompat.Builder(context, channelId)
        customNotification
                .setSmallIcon(R.drawable.ic_stat_name)
                .setCustomContentView(notificationLayout)
                .setCustomBigContentView(bigNotificationLayout)
                .setContentIntent(contentPendingIntent)
                .setChannelId(channelId)
                .setAutoCancel(true)
        if (vibrate) {
            customNotification.setVibrate(longArrayOf(300, 300, 300, 300))
        }
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.O) {
            customNotification.priority = NotificationCompat.PRIORITY_HIGH
        }
        if (led) {
            customNotification.setLights(Color.WHITE, 300, 1000)
        }
        if (sound) {
            customNotification.setSound(uri)
        }
        if (notificationManager != null) {
            PreferenceManager.getDefaultSharedPreferences(context).edit().putInt(context.getString(R.string.dialog_option), contact.category).apply()
            notificationManager.notify(notiId, customNotification.build())
        }
    }

    @JvmStatic
    fun getOTPFromString(body: String): String? {
        var textString = body
        var OTP: String? = null
        textString = textString.replace("[Rr]{1}[Ss]{1}[.]{1}[\\s]?[0-9]*\\.[0-9]*".toRegex(), "")
        textString = textString.replace("[Ii]{1}[Nn]{1}[Rr]{1}[\\s]?[0-9]*\\.[0-9]*".toRegex(), "")
        textString = textString.replace("[a-zA-Z]{1}[0-9]{4}".toRegex(), "")
        val p = Pattern.compile("[0-9]{6}|[0-9]{8}|[0-9]{4}|[0-9]{3}|[0-9]{5}")
        val m = p.matcher(textString)
        if (m.find()) {
            OTP = m.group()
        }
        return OTP
    }
}