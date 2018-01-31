package in.inboxy.threads;

/**
 * Created by rahul1993 on 11/11/2017.
 */

/*
public class MyAyncTask extends AsyncTask<Void,Void,Void> {
  MessageDatabase mDB;
  Application application;

  public MyAyncTask(MessageDatabase mDB, Application application) {
    this.mDB = mDB;
    this.application = application;
  }

  @Override
  protected Void doInBackground(Void... voids) {
    List<Sms> list = MessageUtils.getAllMessages(application);
//    int listsize =list.size();
//    Log.i("myAsyncTask", Integer.toString(listsize));
//    int count = 0;
    Message message = new Message();
    for (int i = 0; i < list.size(); i++) {
      Sms sms = list.get(i);
      PhoneContact.init(application);
      Contact contact = ContactUtils.getContact(sms.address, application, true);
      Message.MessageType type = Sms.MessageType.SENT.compareTo(sms.type) == 0 ? SENT : INBOX;
      final Long timeStamp = MessageUtils.getTimeStamp(sms.receivedDate, sms.sentDate, type);
      message.body = sms.body;
      message.address = sms.address;
      message.read = sms.read;
      message.seen = sms.seen;
      message.threadId = sms.threadId;
      message.type = type;
      message.timestamp = timeStamp;
      message.category = contact.getCategory();
      mDB.messageDao().insertMessage(message);
    }//    Log.i("myAsyncTask", Integer.toString(totalCount));

    return null;
  }

  @Override
  public void onPostExecute(Void result) {
    Toast.makeText(application, "Analysis Complete", Toast.LENGTH_SHORT).show();
    SharedPreferences preferences = PreferenceManager
            .getDefaultSharedPreferences(application);
    preferences.edit().putBoolean(application.getString(R.string.key_sms_categorized), true)
            .apply();
    Intent intent = new Intent(application, MainActivity.class);
    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
    application.startActivity(intent);

  }
}*/
