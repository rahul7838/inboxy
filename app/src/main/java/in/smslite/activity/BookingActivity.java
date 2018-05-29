package in.smslite.activity;

import android.arch.lifecycle.ViewModelProvider;
import android.arch.lifecycle.ViewModelProviders;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import in.smslite.R;
import in.smslite.adapter.BookingAdapter;
import in.smslite.db.Message;
import in.smslite.db.Model.BookingTrain;
import in.smslite.viewModel.BookingActivityViewModel;

/**
 * Created by rahul1993 on 5/29/2018.
 */

public class BookingActivity extends AppCompatActivity {
  @BindView(R.id.booking_recycler_view)
  RecyclerView recyclerView;
  BookingActivityViewModel bookingActivityViewModel;
  @Override
  protected void onCreate(@Nullable Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_booking);
    ButterKnife.bind(this);
    setTitle("Bookings");

    bookingActivityViewModel = ViewModelProviders.of(this).get(BookingActivityViewModel.class);
    List<BookingTrain> list = bookingActivityViewModel.getBookingMessage();
    LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
    recyclerView.setLayoutManager(linearLayoutManager);
    linearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
    recyclerView.setHasFixedSize(true);
    BookingAdapter adapter = new BookingAdapter(list);
    recyclerView.setAdapter(adapter);
  }
}
