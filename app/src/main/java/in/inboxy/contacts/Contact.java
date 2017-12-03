package in.inboxy.contacts;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.support.v4.graphics.drawable.RoundedBitmapDrawable;
import android.support.v4.graphics.drawable.RoundedBitmapDrawableFactory;
import android.text.TextUtils;


public class Contact {

    public static final int UNCATEGORIZED = 0;
    public static final int PRIMARY = 1;
    public static final int FINANCE = 2;
    public static final int PROMOTIONS = 3;
    public static final int UPDATES = 4;
    private static final String TAG = Contact.class.getSimpleName();
//    protected final HashSet<UpdateListener> mListeners = new HashSet<>();
    protected String mName;
    protected int mCategory;
    protected Source mSource;
    protected RoundedBitmapDrawable mAvatar;
    protected byte[] mAvatarData;
    protected String mNumber;
    protected String mThreadId;

    public Contact() {
    }

    /*public void addListener(UpdateListener l) {
        synchronized (mListeners) {
            mListeners.add(l);
        }
    }

    public void removeListener(UpdateListener l) {
        synchronized (mListeners) {
            mListeners.remove(l);
        }
    }

    public void dumpListeners() {
        synchronized (mListeners) {
            int i = 0;
            Log.i(TAG, "[PhoneContact] dumpListeners; size=" + mListeners.size());
            for (UpdateListener listener : mListeners) {
                Log.i(TAG, "["+ (i++) + "]" + listener);
            }
        }
    }*/

    public String getDisplayName() {
        if (TextUtils.isEmpty(mName)) {
            return mNumber;
        } else {
            return mName;
        }
    }

    public synchronized String getNumber() {
        return mNumber;
    }

    public synchronized RoundedBitmapDrawable getAvatar(Context context) {
        if (mAvatar == null) {
            Bitmap b;
            if (mAvatarData != null) {
                b = BitmapFactory.decodeByteArray(mAvatarData, 0, mAvatarData.length);
            } else {
                Drawable drawable = in.inboxy.drawable.DataSource.getInstance(context).getDrawable(getDisplayName());
                b = in.inboxy.utils.DrawableUtils.getBitmap(drawable);
            }
            mAvatar = RoundedBitmapDrawableFactory.create(context.getResources(), b);
            mAvatar.setCircular(true);
        }
        return mAvatar;
    }

    public int getCategory() {
        return this.mCategory;
    }

    public Source getSource() {
        return mSource;
    }

    public String getThreadId() {
        return mThreadId;
    }

    public enum Source {
        PHONE, FIREBASE, OTHER
    }

    /*public interface UpdateListener {
        void onUpdate(Contact contact);
    }

    protected static void callListeners(Contact contact) {
        HashSet<UpdateListener> iterator;
        synchronized (contact.mListeners) {
            iterator = (HashSet<UpdateListener>) contact.mListeners.clone();
        }
        for (UpdateListener l : iterator) {
            l.onUpdate(contact);
        }
    }
*/

}
