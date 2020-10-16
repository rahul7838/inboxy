package `in`.smslite.contacts

import `in`.smslite.drawable.DataSource
import `in`.smslite.utils.DrawableUtils
import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.text.TextUtils
import androidx.core.graphics.drawable.RoundedBitmapDrawable
import androidx.core.graphics.drawable.RoundedBitmapDrawableFactory

open class Contact {
    //    protected final HashSet<UpdateListener> mListeners = new HashSet<>();
    protected var mName: String? = null
    var category = 0
        protected set
    var source: Source? = null
        protected set
    protected var mAvatar: RoundedBitmapDrawable? = null
    protected var mAvatarData: ByteArray? = null

    @get:Synchronized
    var number: String? = null
        protected set
    var threadId: String? = null
        protected set
    val displayName: String?
        get() = if (TextUtils.isEmpty(mName)) {
            number
        } else {
            mName
        }

    @Synchronized
    fun getAvatar(context: Context): RoundedBitmapDrawable {
        if (mAvatar == null) {
            val b: Bitmap
            b = if (mAvatarData != null) {
                BitmapFactory.decodeByteArray(mAvatarData, 0, mAvatarData!!.size)
            } else {
                val drawable = DataSource.getInstance(context).getDrawable(displayName)
                DrawableUtils.getBitmap(drawable)
            }
            mAvatar = RoundedBitmapDrawableFactory.create(context.resources, b)
            mAvatar!!.isCircular = true
        }
        return mAvatar!!
    }

    enum class Source {
        PHONE, FIREBASE, OTHER
    }

    companion object {
        const val UNCATEGORIZED = 0
        const val PRIMARY = 1
        const val FINANCE = 2
        const val PROMOTIONS = 3
        const val UPDATES = 4
        const val BLOCKED = 5
        const val ARCHIVE = 6
        private val TAG = Contact::class.java.simpleName
    }
}