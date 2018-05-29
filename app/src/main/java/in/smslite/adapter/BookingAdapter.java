package in.smslite.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.List;

import in.smslite.R;
import in.smslite.db.Message;
import in.smslite.db.Model.BookingTrain;
import in.smslite.viewHolder.BookingViewHolder;

/**
 * Created by rahul1993 on 5/29/2018.
 */

public class BookingAdapter extends RecyclerView.Adapter<BookingViewHolder> {
private List<BookingTrain> list;

public BookingAdapter(List<BookingTrain> list){
  this.list = list;
}
  @Override
  public BookingViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
    View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.card_booking_item, parent, false);
    return new BookingViewHolder(view);
  }

  @Override
  public void onBindViewHolder(BookingViewHolder holder, int position) {
    holder.setPnrNumber(list.get(position));
    holder.setTrainNumber(list.get(position));
  }

  @Override
  public int getItemCount() {
    return list.size();
  }
}
