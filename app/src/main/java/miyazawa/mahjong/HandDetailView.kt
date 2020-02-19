package miyazawa.mahjong

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.widget.LinearLayout
import kotlinx.android.synthetic.main.hand_detail.view.*

class HandDetailView : LinearLayout {
    constructor(context: Context?) : super(context)

    constructor(context: Context?,
                    attrs: AttributeSet): super(context, attrs)

    constructor(context: Context?,
                attrs: AttributeSet?,
                defStyleAttr: Int): super(context, attrs, defStyleAttr)

    constructor(context: Context?,
                attrs: AttributeSet?,
                defStyleAttr: Int,
                defStyleRes: Int): super(context, attrs, defStyleAttr, defStyleRes)


    init {
        LayoutInflater.from(context).inflate(R.layout.hand_detail, this)
    }

    fun setSubScoreText(text: String, value: Int?) {
        textHandNameDetail.text = text
        textHandValueDetail.text = if (value != null) "${value} 符" else "-"
    }

    fun setText(text: String, value: Int) {
        textHandNameDetail.text = text
        textHandValueDetail.text = if (value < 0) "役満" else "${value} 翻"
    }
}