/*
package in.smslite.activity;

import android.support.v7.widget.RecyclerView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.robolectric.Robolectric;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;

import java.util.List;

import in.smslite.BuildConfig;
import in.smslite.db.Message;

import static org.mockito.Matchers.contains;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;


//Created by rahul1993 on 2/28/2018.
@RunWith(RobolectricTestRunner.class)
@Config(constants = BuildConfig.class)
public class MainActivityTest {

  private MainActivity activity;
  @Mock
  private List<Message> messageList;

  private int category = 1;
  @Mock
  private RecyclerView recyclerView;
  @Mock
  private RelativeLayout  emptyView;
  @Mock
  TextView emptyText;
//  @Mock
//  private ImageView emptyImage;

  @Before
  public void setUp() throws Exception {
    MockitoAnnotations.initMocks(this);
    activity = Robolectric.setupActivity(MainActivity.class);
  }

  @Test
  public void setMessageList_verify() {
//    when(messageList.isEmpty()).thenReturn(true);
    when(messageList.isEmpty()).thenReturn(true);
    activity.setMessageList(messageList, category);

    String emptyTextTitle = "inbox is empty";
    verify(emptyText).setText(contains(emptyTextTitle));
//    verify(emptyImageView).setImageDrawable(eq());
//    verify(recyclerView).setVisibility(eq(View.INVISIBLE));
//    verify(emptyView).setVisibility(eq(View.VISIBLE));
  }
}
*/
