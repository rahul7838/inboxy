package in.smslite.viewHolder;

import android.content.Context;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;
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
  private LinearLayout linearLayout;
  private static TextView smsStatusTextView;
  private static Context mContext;

  public CompleteSmsSentViewHolder(View itemView, Context context) {
    super(itemView);
    view = itemView;
    this.mContext = context;
    linearLayout = itemView.findViewById(R.id.card_sent_linear_layout_id);
    completeMsg = (TextView) view.findViewById(R.id.body);
    timeView = (TextView) view.findViewById(R.id.timestamp);
    smsStatusTextView = (TextView) view.findViewById(R.id.user_sms_status_id);
//    smsStatusTextView = view.findViewById(R.id.notify_user_sms_status_id);
    smsStatusTextView.setOnClickListener(this);
  }

  public void setCompleteMsg(String body) {
    completeMsg.setText(body);
  }

  public void setTime(long x) {
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

  public void setItemBackgroundBlack() {
    linearLayout.setBackgroundResource((R.drawable.sms_sent_background_black));
  }

  public void setItemBackgroundBlue() {
    linearLayout.setBackgroundResource(R.drawable.sms_sent_background);
  }

  public void setBackgroundColorWhite() {
    view.setBackgroundColor(mContext.getResources().getColor(R.color.white_pure));
  }

  public void setBackgroundColor() {
    view.setBackgroundColor(mContext.getResources().getColor(R.color.item_selected));
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

