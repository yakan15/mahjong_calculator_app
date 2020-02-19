package miyazawa.mahjong

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.widget.LinearLayout
import kotlinx.android.synthetic.main.hand_detail.view.*
import kotlinx.android.synthetic.main.hand_image_with_text.view.*

class HandImageWithTextView : LinearLayout{
    constructor(context: Context?) : super(context)

    constructor(context: Context?,
                attrs: AttributeSet
    ): super(context, attrs)

    constructor(context: Context?,
                attrs: AttributeSet?,
                defStyleAttr: Int): super(context, attrs, defStyleAttr)

    init {
        LayoutInflater.from(context).inflate(R.layout.hand_image_with_text, this)
    }

    fun setHandImage(i : Int) {
        handImage.setHandImage(i)
    }

    fun setText(text: String) {
        handImageText.text = text
    }
}