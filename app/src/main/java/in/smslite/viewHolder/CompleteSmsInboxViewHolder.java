package in.smslite.viewHolder;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import java.util.Date;

import in.smslite.R;
import in.smslite.utils.TimeUtils;

public class CompleteSmsInboxViewHolder extends RecyclerView.ViewHolder {
  private static Context mContext;
  private TextView bodyView, timestampView;
    private View itemView;

  public CompleteSmsInboxViewHolder(View view,  Context context) {
    super(view);
    this.mContext = context;
    itemView = view;
      bodyView = (TextView) itemView.findViewById(R.id.body);
      timestampView = (TextView) itemView.findViewById(R.id.timestamp);
  }

  public void setCompleteMsg(String body){
      bodyView.setText(body);
  }

  public void setTime(long x){
//    Date date = new Date(x);
      String prettyTime = TimeUtils.getPrettyElapsedTime(x);
      timestampView.setText(prettyTime);

  }

  public  void setBackgroundColorWhite() {
    itemView.setBackgroundColor(mContext.getResources().getColor(R.color.white_pure));
  }

  public void setBackgroundColor() {
    itemView.setBackgroundColor(mContext.getResources().getColor(R.color.item_selected));
  }

}
