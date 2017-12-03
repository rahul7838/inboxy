package in.inboxy.drawable;

import android.content.Context;

import in.inboxy.utils.ColorGenerator;


class DrawableProvider {

  static final int SAMPLE_ROUND = 2;

  private final ColorGenerator mGenerator;
  private Context mContext;

  DrawableProvider(Context context) {

    mContext = context;
    mGenerator = ColorGenerator.MATERIAL;
  }

  TextDrawable getSampleRound(String text) {
    return TextDrawable.builder().buildRound(text.substring(0, 1), mGenerator.getColor(text));
  }

}


