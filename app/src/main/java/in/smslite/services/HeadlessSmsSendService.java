package in.smslite.services;

import android.app.IntentService;
import android.content.Intent;
import android.support.annotation.Nullable;
import android.util.Log;

/**
 * Created by rahul1993 on 4/10/2018.
 */

public class HeadlessSmsSendService extends IntentService {

  private static final String TAG = HeadlessSmsSendService.class.getSimpleName();
  /**
   * Creates an IntentService.  Invoked by your subclass's constructor.
   *
   * name Used to name the worker thread, important only for debugging.
   */
  public HeadlessSmsSendService() {
    super("HeadlessSmsSendService");
  }

  @Override
  protected void onHandleIntent(@Nullable Intent intent) {
    assert intent != null;
    if(intent.getData()!=null){

    } else {
      return;
    }
    Log.i(TAG, "respond to incoming phone calls");
  }
}
