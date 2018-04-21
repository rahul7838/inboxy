package in.smslite.viewHolder;

import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import in.smslite.R;
import in.smslite.activity.CompleteSmsActivity;
import in.smslite.utils.TimeUtils;


public class CompleteSmsSentViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
  private static final String TAG = CompleteSmsSentViewHolder.class.getSimpleName();
  public static boolean tryFailedSms;
  private Long failedSmsTime;
  private View view;
  private TextView completeMsg, timeView;
  private static TextView smsStatusTextView;
  public CompleteSmsSentViewHolder(View itemView) {
    super(itemView);
    view = itemView;
    completeMsg = (TextView) view.findViewById(R.id.body);
    timeView = (TextView) view.findViewById(R.id.timestamp);
    smsStatusTextView = (TextView) view.findViewById(R.id.user_sms_status_id);
//    smsStatusTextView = view.findViewById(R.id.notify_user_sms_status_id);
    smsStatusTextView.setOnClickListener(this);
  }

  public void setCompleteMsg(String body){
    completeMsg.setText(body);
  }

  public void setTime(long x){
//    Date date = new Date(x);
    this.failedSmsTime = x;
    String timea = TimeUtils.getPrettyElapsedTime(x);
    timeView.setText(timea);
  }

  @Override
  public void onClick(View v) {
    Log.d(TAG, "trySendFailedSms");
    Log.d(TAG, Long.toString(failedSmsTime));
    tryFailedSms = true;
    CompleteSmsActivity.sendTextSms(failedSmsTime);


  }

//  public void setSmsStatus(String text){
//    smsStatusTextView.setText(text);
//    smsStatusTextView.setVisibility(View.VISIBLE);
//  }


//  public static void smsStatusVisiblity(String text) {
//    smsStatusTextView.setText(text);
//    smsStatusTextView.setVisibility(View.VISIBLE);
//  }
}

