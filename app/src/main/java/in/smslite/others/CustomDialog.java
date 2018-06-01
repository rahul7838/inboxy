package in.smslite.others;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.view.ActionMode;
import android.view.View;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import in.smslite.R;
import in.smslite.contacts.Contact;
import in.smslite.db.Message;
import in.smslite.utils.ThreadUtils;

import static in.smslite.others.MainActivityHelper.mActionMode;

/**
 * Created by rahul1993 on 5/31/2018.
 */

public class CustomDialog extends Dialog {

  @BindView(R.id.dialog_heading1)
  TextView heading;
  @BindView(R.id.dialog_option_blocked_image)
  ImageView blockedImage;
  @BindView(R.id.dialog_option_blocked)
  TextView blockedOption;
  @BindView(R.id.dialog_option_archive)
  TextView archivedOption;
  @BindView(R.id.dialog_option_archive_image)
  ImageView archiveImage;
  @BindView(R.id.dialog_cancel)
  TextView cancelOption;
  @BindView(R.id.dialog_option_primary)
  TextView primaryOption;
  @BindView(R.id.dialog_option_primary_image)
  ImageView primaryImage;
  @BindView(R.id.dialog_option_finance)
  TextView financeOption;
  @BindView(R.id.dialog_option_finance_image)
  ImageView financeImage;
  @BindView(R.id.dialog_option_promotion)
  TextView promotionOption;
  @BindView(R.id.dialog_option_promotion_image)
  ImageView promotionImage;
  @BindView(R.id.dialog_option_update)
  TextView updateOption;
  @BindView(R.id.dialog_option__update_image)
  ImageView updateImage;
  @BindView(R.id.dialog_checkbox)
  CheckBox checkBox;

  private List<Message> selectedItem;
  private ActionMode.Callback callback;
  private String whichActivity;

  public CustomDialog(@NonNull Context context, List<Message> selectedItem, ActionMode.Callback callback, String whichActivity) {
    super(context);
    this.selectedItem = selectedItem;
    this.callback = callback;
    this.whichActivity = whichActivity;
  }

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.custom_dialog);
    ButterKnife.bind(this);
    dialogOptionClickListener();
    setVisibility();
    boolean checked = checkBox.isChecked();
    PreferenceManager.getDefaultSharedPreferences(getContext()).edit().putBoolean
        (getContext().getResources().getString(R.string.dialog_checkbox), checked).apply();
    heading.setText("Move " + selectedItem.size() + " conversation to");
  }

  private void setVisibility() {
    int value = PreferenceManager.getDefaultSharedPreferences(getContext()).getInt(getContext().getString(R.string.dialog_option), Contact.PRIMARY);
    switch (value) {
      case Contact.PRIMARY:
        primaryOption.setVisibility(View.GONE);
        primaryImage.setVisibility(View.GONE);
        break;
      case Contact.FINANCE:
        financeOption.setVisibility(View.GONE);
        financeImage.setVisibility(View.GONE);
        break;
      case Contact.PROMOTIONS:
        promotionOption.setVisibility(View.GONE);
        promotionImage.setVisibility(View.GONE);
        break;
      case Contact.UPDATES:
        updateOption.setVisibility(View.GONE);
        updateImage.setVisibility(View.GONE);
        break;
      case Contact.ARCHIVE:
        archivedOption.setVisibility(View.GONE);
        archiveImage.setVisibility(View.GONE);
        break;
      case Contact.BLOCKED:
        blockedOption.setVisibility(View.GONE);
        blockedImage.setVisibility(View.GONE);
        break;
      default:
    }
  }

  private void dialogOptionClickListener() {
    blockedOption.setOnClickListener((View v) -> {
      int category = Contact.BLOCKED;
//      int length = selectedItem.size();
//      for (int i = 0; i < length; i++) {
        new ThreadUtils.UpdateMessageCategoryToBlocked(getContext(), selectedItem, category).run();
//      }
      callback.onDestroyActionMode(mActionMode);
      onBackPressed();
    });

    archivedOption.setOnClickListener(v -> startThreadToUpdateCategory(Contact.ARCHIVE));

    primaryOption.setOnClickListener(v -> startThreadToUpdateCategory(Contact.PRIMARY));

    financeOption.setOnClickListener(v -> startThreadToUpdateCategory(Contact.FINANCE));

    promotionOption.setOnClickListener(v -> startThreadToUpdateCategory(Contact.PROMOTIONS));

    updateOption.setOnClickListener(v -> startThreadToUpdateCategory(Contact.UPDATES));

    cancelOption.setOnClickListener(v -> {
      onBackPressed();
      callback.onDestroyActionMode(mActionMode);
    });
  }

  private void startThreadToUpdateCategory(int category){
    new ThreadUtils.UpdateMessageCategoryToBlocked(getContext(), selectedItem, category).run();
    onBackPressed();
    callback.onDestroyActionMode(mActionMode);
  }

  @Override
  public void onDetachedFromWindow() {
    super.onDetachedFromWindow();
    selectedItem.clear();
  }
}
