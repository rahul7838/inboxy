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
  private int INBOX = 0;
  private int SENT = 1;
  private int FAILED = 2;
  private int QUEUED = 3;
  private int OUTBOX= 4;

  private List<Message> SmsConversation;

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
    else if(viewType == FAILED){
      View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.card_sent_failed_sms, parent, false);
      return new CompleteSmsSentViewHolder(view);
    } else if(viewType == QUEUED) {
      View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.card_sending_sms, parent, false);
      return new CompleteSmsSentViewHolder(view);
    } else {
      View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.card_delivered_sms, parent, false);
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
    } else if(Message.MessageType.QUEUED.equals(SmsConversation.get(position).type)){
      return QUEUED;
    } else if(Message.MessageType.OUTBOX.equals(SmsConversation.get(position).type)){
      return OUTBOX;
    }
    return SENT;
  }


}
