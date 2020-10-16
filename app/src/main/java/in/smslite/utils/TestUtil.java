package in.smslite.utils;

/**
 * Created by rahul1993 on 4/19/2018.
 */

public class TestUtil {

  private static final String TAG = TestUtil.class.getSimpleName();

//  SmsReceiver smsReceiver = new SmsReceiver();
//  smsReceiver.onReceive();
//  public static void TestOTP(Context context) {
//    List<Message> msg = db.messageDao().getOTPFOrTest();
//    int size = msg.size();
//    int counter = 0;
//    Message message = new Message();
//    for (int i = 0; i < size; i++) {
//      counter++;
//      Message sms = msg.get(i);
//      message.body = sms.body;
//      message.address = sms.address;
//      message.timestamp = sms.timestamp;
//      Intent intent = new Intent();
//      intent.setAction("in.smslite.utils.TEST_NOTIFICATION");
//      Bundle bundle = new Bundle();
//      bundle.putSerializable("sms", (Serializable) message );
//      intent.putExtra("bundle", bundle);
//      LocalBroadcastManager localBroadcastManager = LocalBroadcastManager.getInstance(context);
//      context.sendBroadcast(intent);
//      try {
//        Thread.sleep(4000);
//      } catch (InterruptedException e) {
//        e.printStackTrace();
//      }
//    }
//    Log.d(TAG, Integer.toString(counter));
//  }

 /* public static BroadcastReceiver testNotiBroadCast = new BroadcastReceiver() {
    @Override
    public void onReceive(Context context, Intent intent) {
      Log.d(TAG, "testNotiExecuted");
      Bundle bundle =  intent.getBundleExtra("bundle");
      Message sms = (Message) bundle.getSerializable("sms");
      Contact contact = ContactUtils.getContact(sms.getAddress(), context, true);
      Message message = new Message();
      Boolean customNotification = false;
      String number = "";
//        bodyText.append(sms.getMessageBody());
//        body = bodyText.toString();
        if (contact.getCategory() == Contact.PRIMARY) {
          number = ContactUtils.normalizeNumber(sms.getAddress());
        } else {
          number = sms.getAddress();
        }
        message.body = sms.getBody();
        message.address = number;
        message.read = false;
        message.seen = false;
        message.timestamp = 123454;
        message.threadId = 123;
        message.type = Message.MessageType.INBOX;
        message.category = contact.getCategory();


      new BroadcastMessageAsyncTask(message, contact, customNotification).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, context);

    }
  };*/

  //below code is used to test the OTP notification
//    registerReceiver(TestUtil.testNotiBroadCast, new IntentFilter("in.smslite.utils.TEST_NOTIFICATION"));
//    Thread thread = new Thread() {
//      @Override
//      public void run() {
//        super.run();
//        TestUtil.TestOTP(context);
//      }
//    };
//    thread.start();
}
