package in.smslite.services;

import android.app.IntentService;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;

import in.smslite.db.MessageDatabase;

/**
 * Created by rahul1993 on 3/18/2018.
 */

public class SwipeToDismissNoti extends IntentService {
  /**
   * Creates an IntentService.  Invoked by your subclass's constructor.
   *
   * @param name Used to name the worker thread, important only for debugging.
   */
  public SwipeToDismissNoti() {
    super("SwipeToDismissNoti");
  }

  @Override
  protected void onHandleIntent(@Nullable Intent intent) {
    MessageDatabase db = MessageDatabase.getInMemoryDatabase(getBaseContext());
    Bundle bundle = intent.getExtras();
    String category = bundle.getString("Category");
    db.messageDao().markAllSeen(Integer.valueOf(category))        ;

  }
}
