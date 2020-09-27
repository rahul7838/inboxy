package in.smslite.viewHolder;

import android.preference.PreferenceManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import in.smslite.R;
import in.smslite.adapter.CompleteSmsAdapter;
import in.smslite.utils.TimeUtils;


public class CompleteSmsSentViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
  private static final String TAG = CompleteSmsSentViewHolder.class.getSimpleName();
  public static boolean tryFailedSms;
  private Long failedSmsTime;
  private View view;
  private TextView completeMsg, timeView;
  private LinearLayout linearLayout;
  private TextView smsStatusTextView;
  private String address;
  private CompleteSmsAdapter.SendTextSms sendTextSmsListener;
  public CompleteSmsSentViewHolder(View itemView, String address, CompleteSmsAdapter.SendTextSms sendTextSmsListener) {
    super(itemView);
    view = itemView;
    this.address = address;
    this.sendTextSmsListener = sendTextSmsListener;

    linearLayout = itemView.findViewById(R.id.card_sent_linear_layout_id);
    completeMsg = (TextView) view.findViewById(R.id.body);
    timeView = (TextView) view.findViewById(R.id.timestamp);
    smsStatusTextView = (TextView) view.findViewById(R.id.user_sms_status_id);
    smsStatusTextView.setOnClickListener(this);
  }

  public void setCompleteMsg(String body) {
    completeMsg.setText(body);
  }

  public void setTime(long x) {
    this.failedSmsTime = x;
    String timea = TimeUtils.getPrettyElapsedTime(x);
    timeView.setText(timea);
  }

  @Override
  public void onClick(View v) {
    Log.d(TAG, "trySendFailedSms");
    Log.d(TAG, Long.toString(failedSmsTime));
    tryFailedSms = true;
    int category = PreferenceManager.getDefaultSharedPreferences(v.getContext()).getInt(view.getContext().getString(R.string.dialog_option),0);
    sendTextSmsListener.onSendTextSms( failedSmsTime, address, category);
  }

  public void setItemBackgroundBlack() {
    linearLayout.setBackgroundResource((R.drawable.sms_sent_background_black));
  }

  public void setItemBackgroundBlue() {
    linearLayout.setBackgroundResource(R.drawable.sms_sent_background);
  }

  public void setBackgroundColorWhite() {
    view.setBackgroundColor(view.getContext().getResources().getColor(R.color.white_pure));
  }

  public void setBackgroundColor() {
    view.setBackgroundColor(view.getContext().getResources().getColor(R.color.item_selected));
  }

}

