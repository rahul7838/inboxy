package in.inboxy.drawable;

import android.content.Context;
import android.graphics.drawable.Drawable;

import java.util.HashMap;
import java.util.Map;

import in.inboxy.R;


public class DataSource {
  private static final String TAG = "DataSource";
  private static DataSource dataSource;
  private Map<String, Drawable> drawableMap;
  private DrawableProvider mProvider;
  private static Drawable defaultDrawable;

  private DataSource(Context context) {
    drawableMap = new HashMap<>();
    mProvider = new DrawableProvider(context);
    defaultDrawable = context.getResources().getDrawable(R.drawable.ic_account, null);
  }

  public static DataSource getInstance(Context context) {
    if (dataSource == null) {
      dataSource = new DataSource(context);
    }
    return dataSource;
  }

  public Drawable getDrawable(String displayName) {
    if (drawableMap.containsKey(displayName)) {
      return drawableMap.get(displayName);
    } else if (displayName != null && isAlphabet(displayName.charAt(0))) {
      Drawable drawable = mProvider.getSampleRound(displayName);
      drawableMap.put(displayName, drawable);
      return drawable;
    }
    return defaultDrawable;
  }

  private boolean isAlphabet(char c) {
    return (c >= 'a' && c <= 'z') ||
            (c >= 'A' && c <= 'Z');
  }
}

