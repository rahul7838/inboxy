package in.smslite.viewHolder;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import org.w3c.dom.Text;

import java.util.Date;

import in.smslite.R;
import in.smslite.utils.TimeUtils;


public class CompleteSmsSentViewHolder extends RecyclerView.ViewHolder {
  private View view;
  private TextView completeMsg, timeView;
  private static TextView smsStatusTextView;
  public CompleteSmsSentViewHolder(View itemView) {
    super(itemView);
    view = itemView;
    completeMsg = (TextView) view.findViewById(R.id.body);
    timeView = (TextView) view.findViewById(R.id.timestamp);
    smsStatusTextView = view.findViewById(R.id.notify_user_sms_status_id);
  }

  public void setCompleteMsg(String body){
    completeMsg.setText(body);
  }

  public void setTime(long x){
//    Date date = new Date(x);
    String timea = TimeUtils.getPrettyElapsedTime(x);
    timeView.setText(timea);
  }

  public void setSmsStatus(String text){
    smsStatusTextView.setText(text);
//    smsStatusTextView.setVisibility(View.VISIBLE);
  }


  public static void smsStatusVisiblity(String text) {
    smsStatusTextView.setText(text);
    smsStatusTextView.setVisibility(View.VISIBLE);
  }
}

