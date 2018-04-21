package in.smslite.adapter;

import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.List;

import in.smslite.R;
import in.smslite.db.Message;
import in.smslite.viewHolder.CompleteSmsInboxViewHolder;
import in.smslite.viewHolder.CompleteSmsSentViewHolder;

public class CompleteSmsAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
  private static final String TAG = CompleteSmsAdapter.class.getSimpleName();
  int INBOX = 0;
  int SENT = 1;
  int FAILED = 2;
  List<Message> SmsConversation;

  public CompleteSmsAdapter(List<Message> SmsConversation) {
    this.SmsConversation = SmsConversation;
  }

  @Override
  public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
    if (viewType == INBOX) {
      View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.card_inbox, parent, false);
      return new CompleteSmsInboxViewHolder(view);
    }
    else if (viewType == SENT){
      View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.card_sent, parent, false);
      return new CompleteSmsSentViewHolder(view);
    }
    else {
      View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.card_sent_failed_sms, parent, false);
      return new CompleteSmsSentViewHolder(view);
    }
  }

  @Override
  public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
    if (holder instanceof CompleteSmsInboxViewHolder) {
      CompleteSmsInboxViewHolder completeSmsViewHolder = (CompleteSmsInboxViewHolder) holder;
      completeSmsViewHolder.setCompleteMsg(SmsConversation.get(position).body);
      completeSmsViewHolder.setTime(SmsConversation.get(position).timestamp);
    }
    else {
      CompleteSmsSentViewHolder completeSmsSentViewHolder = (CompleteSmsSentViewHolder) holder;
      completeSmsSentViewHolder.setCompleteMsg(SmsConversation.get(position).body);
      completeSmsSentViewHolder.setTime(SmsConversation.get(position).timestamp);
//      completeSmsSentViewHolder.setSmsStatus("text");
    }
  }

  @Override
  public int getItemCount() {
    return SmsConversation.size();
  }

  @Override
  public int getItemViewType(int position) {
    if (Message.MessageType.INBOX.equals(SmsConversation.get(position).type)) {
      return INBOX;
    } else if (Message.MessageType.FAILED.equals(SmsConversation.get(position).type)){
      return FAILED;
    }
    return SENT;
  }


}
