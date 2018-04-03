package in.smslite.viewHolder;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import java.util.Date;

import in.smslite.R;
import in.smslite.utils.TimeUtils;


public class CompleteSmsSentViewHolder extends RecyclerView.ViewHolder {
  private View view;
  private TextView completeMsg, timeView;

  public CompleteSmsSentViewHolder(View itemView) {
    super(itemView);
    view = itemView;
    completeMsg = (TextView) view.findViewById(R.id.body);
    timeView = (TextView) view.findViewById(R.id.timestamp);
  }

  public void setCompleteMsg(String body){
    completeMsg.setText(body);
  }

  public void setTime(long x){
//    Date date = new Date(x);
    String timea = TimeUtils.getPrettyElapsedTime(x);
    timeView.setText(timea);

  }
}

