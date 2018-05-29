package in.smslite.viewHolder;

import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import org.w3c.dom.Text;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Optional;
import in.smslite.R;
import in.smslite.db.Model.BookingTrain;

/**
 * Created by rahul1993 on 5/29/2018.
 */

public class BookingViewHolder extends RecyclerView.ViewHolder {
  @Nullable
  @BindView(R.id.booking_heading)
  TextView headingView;

  @Nullable
  @BindView(R.id.booking_time_date)
  TextView timeDate;

 @Nullable
  @BindView(R.id.booking_pnr_number)
  TextView pnrNumber;

  @Nullable
  @BindView(R.id.booking_pnr_status_number)
  TextView pnrStatusNumber;

  @Nullable
  @BindView(R.id.booking_train_number)
  TextView trainNumber;

  public BookingViewHolder(View itemView) {
    super(itemView);
    ButterKnife.bind(this, itemView);
  }

  public void setPnrNumber(BookingTrain bookingTrain){
    pnrNumber.setText(bookingTrain.getPnrNumber());
  }

  public void setTrainNumber(BookingTrain bookingTrain){
    trainNumber.setText(bookingTrain.getTrainNumber());
  }
}
