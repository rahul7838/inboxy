package in.smslite.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
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

  public Context context;
  public List<Message> smsConversation;
  public List<Message> selectedItemAdapter = new ArrayList<>();
  public List<Message> listOfItemAdapter = new ArrayList<>();
  private String address;

  public CompleteSmsAdapter(List<Message> SmsConversation, String address, Context context, List<Message> selectedItemAdapter, List<Message> listOfItemAdapter) {
    this.smsConversation = SmsConversation;
    this.context = context;
    this.selectedItemAdapter = selectedItemAdapter;
    this.listOfItemAdapter = listOfItemAdapter;
    this.address = address;
  }

  @Override
  public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
    if (viewType == INBOX) {
      View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.card_inbox, parent, false);
      return new CompleteSmsInboxViewHolder(view, context);
    }
    else if (viewType == SENT){
      View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.card_sent, parent, false);
      return new CompleteSmsSentViewHolder(view, address, context);
    }
    else if(viewType == FAILED){
      View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.card_sent_failed_sms, parent, false);
      return new CompleteSmsSentViewHolder(view, address,context);
    } else if(viewType == QUEUED) {
      View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.card_sending_sms, parent, false);
      return new CompleteSmsSentViewHolder(view, address,context);
    } else {
      View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.card_delivered_sms, parent, false);
      return new CompleteSmsSentViewHolder(view, address,context);
    }
  }

  @Override
  public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {

    if (holder instanceof CompleteSmsInboxViewHolder) {
      CompleteSmsInboxViewHolder completeSmsViewHolder = (CompleteSmsInboxViewHolder) holder;
      if(selectedItemAdapter.contains(listOfItemAdapter.get(position))){
        completeSmsViewHolder.setBackgroundColor();
      } else {
        completeSmsViewHolder.setBackgroundColorWhite();
      }
      completeSmsViewHolder.setCompleteMsg(smsConversation.get(position).body);
      completeSmsViewHolder.setTime(smsConversation.get(position).timestamp);
    }
    else {
      CompleteSmsSentViewHolder completeSmsSentViewHolder = (CompleteSmsSentViewHolder) holder;
      if(selectedItemAdapter.contains(listOfItemAdapter.get(position))){
        completeSmsSentViewHolder.setBackgroundColor();
        completeSmsSentViewHolder.setItemBackgroundBlack();
      } else {
        completeSmsSentViewHolder.setBackgroundColorWhite();
        completeSmsSentViewHolder.setItemBackgroundBlue();
      }
      completeSmsSentViewHolder.setCompleteMsg(smsConversation.get(position).body);
      completeSmsSentViewHolder.setTime(smsConversation.get(position).timestamp);
//      completeSmsSentViewHolder.setSmsStatus("text");
    }
  }

  public void getColor(RecyclerView.ViewHolder holder, int position){

  }

  @Override
  public int getItemCount() {
    return smsConversation.size();
  }

  @Override
  public int getItemViewType(int position) {
    if (Message.MessageType.INBOX.equals(smsConversation.get(position).type)) {
      return INBOX;
    } else if (Message.MessageType.FAILED.equals(smsConversation.get(position).type)){
      return FAILED;
    } else if(Message.MessageType.QUEUED.equals(smsConversation.get(position).type)){
      return QUEUED;
    } else if(Message.MessageType.OUTBOX.equals(smsConversation.get(position).type)){
      return OUTBOX;
    }
    return SENT;
  }

  public void setMessage(List<Message> message){
    this.smsConversation = message;
    notifyDataSetChanged();
  }

}
