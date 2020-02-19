package miyazawa.mahjong

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.drawable.Drawable
import android.text.TextPaint
import android.util.AttributeSet
import android.view.View
import android.widget.ImageView
import miyazawa.mahjong.model.Constants.TILE_STRING
import miyazawa.mahjong.utils.logd
import okio.utf8Size
import java.lang.Exception
import java.lang.reflect.Field

/**
 * TODO: document your custom view class.
 */
class HandImageView : ImageView {

    companion object {
        private fun getResId(name: String) : Int? {
            try {
                var _name = name
                if (name.length == 1) {
                    _name += name
                }
                val idField: Field = R.drawable::class.java.getDeclaredField(_name.toLowerCase())
                return idField.getInt(idField)
            } catch (e: Exception) {
                return null
            }
        }
    }

    override fun onAttachedToWindow() {
        super.onAttachedToWindow()
        setPadding(5,0,0,0)
    }



    constructor(context: Context) : super(context) {
        init(null, 0)
    }

    constructor(context: Context, attrs: AttributeSet) : super(context, attrs) {
        init(attrs, 0)
    }

    constructor(context: Context, attrs: AttributeSet, defStyle: Int) : super(
        context,
        attrs,
        defStyle
    ) {
        init(attrs, defStyle)
    }

    private fun init(attrs: AttributeSet?, defStyle: Int) {
        // Load attributes
//        val a = context.obtainStyledAttributes(
//            attrs, R.styleable.HandImageView, defStyle, 0
//        )
//        a.recycle()
    }

    fun setHandImage(i: Int) {
        TILE_STRING[i]?.let {tileId ->
            getResId(tileId)?.let {imgId ->
                logd("set image ${imgId}")
                setImageResource(imgId)
            }

        }
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
    }
}
