package in.inboxy.viewHolder;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import java.util.Date;

import in.inboxy.R;
import in.inboxy.utils.TimeUtils;

public class CompleteSmsInboxViewHolder extends RecyclerView.ViewHolder {
    private TextView bodyView, timestampView;

  public CompleteSmsInboxViewHolder(View view) {
    super(view);
      bodyView = (TextView) view.findViewById(R.id.body);
      timestampView = (TextView) view.findViewById(R.id.timestamp);
  }

  public void setCompleteMsg(String body){
      bodyView.setText(body);
  }

  public void setTime(long x){
    Date date = new Date(x);
      String prettyTime = TimeUtils.getPrettyElapsedTime(date);
      timestampView.setText(prettyTime);

  }

}
