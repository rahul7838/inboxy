package in.inboxy.utils;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.support.v4.graphics.drawable.RoundedBitmapDrawable;
import android.support.v4.graphics.drawable.RoundedBitmapDrawableFactory;

import java.io.IOException;
import java.io.InputStream;

public class DrawableUtils {

    /**
     * get bytes array from Uri.
     *
     * @param context current context.
     * @param uri     uri fo the file to read.
     * @return a bytes array.
     * @throws IOException
     */
    public static Drawable getDrawable(Context context, String uri) throws IOException {
        InputStream iStream = context.getContentResolver().openInputStream(Uri.parse(uri));
        RoundedBitmapDrawable roundedBitmapDrawable = RoundedBitmapDrawableFactory.create(context.getResources(), iStream);
        roundedBitmapDrawable.setCircular(true);
        roundedBitmapDrawable.setAntiAlias(true);
        return roundedBitmapDrawable;
    }

    public static Bitmap getBitmap(Drawable drawable) {
        if (drawable instanceof BitmapDrawable) {
            return ((BitmapDrawable) drawable).getBitmap();
        }

        Canvas canvas = new Canvas();
        Bitmap bitmap = Bitmap.createBitmap(72, 72, Bitmap.Config.ARGB_8888);
        canvas.setBitmap(bitmap);
        drawable.setBounds(0, 0, 72, 72);
        drawable.draw(canvas);

        return bitmap;
    }

}
