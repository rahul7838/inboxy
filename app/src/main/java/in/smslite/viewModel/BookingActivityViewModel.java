package in.smslite.viewModel;

import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.database.Cursor;
import android.support.annotation.NonNull;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import in.smslite.db.Message;
import in.smslite.db.MessageDatabase;
import in.smslite.db.Model.BookingTrain;

/**
 * Created by rahul1993 on 5/29/2018.
 */

public class BookingActivityViewModel extends AndroidViewModel {
  private static final String TAG = BookingActivityViewModel.class.getSimpleName();
  private MessageDatabase mDb;
  public BookingActivityViewModel(@NonNull Application application) {
    super(application);
    mDb = MessageDatabase.getInMemoryDatabase(application);
  }

  public List<BookingTrain> getBookingMessage(){
    List<BookingTrain> bookingList = new ArrayList<>();
    List<Message> list = mDb.messageDao().getBookingMessage();
    Log.d(TAG, Integer.toString(list.size()) + " Size");
    int size = list.size();
    for(int i = 0; i<size; i++){
      BookingTrain bookingTrain = new BookingTrain();
      String body = list.get(i).getBody();
      Pattern pattern = Pattern.compile("[0-9]{10}");
      Matcher m = pattern.matcher(body);
      if(m.find()){
        bookingTrain.setPnrNumber(m.group());
      }
      Pattern trainNumber = Pattern.compile("[0-9]{5}");
      Matcher trainNumberMatcher = trainNumber.matcher(body);
      if(trainNumberMatcher.find()){
        bookingTrain.setTrainNumber(trainNumberMatcher.group());
        Log.d(TAG, trainNumberMatcher.group());
      }

      Pattern trainDepature = Pattern.compile("[0-9]{5}");
      Matcher trainDMatcher = trainDepature.matcher(body);
      if(trainNumberMatcher.find()){
        bookingTrain.setTrainNumber(trainNumberMatcher.group());
        Log.d(TAG, trainNumberMatcher.group());
      }

      bookingList.add(bookingTrain);
    }
    return bookingList;
  }
}
