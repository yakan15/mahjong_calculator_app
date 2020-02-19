package miyazawa.mahjong

import android.content.Context
import android.util.AttributeSet
import android.view.Gravity
import android.view.View
import android.widget.LinearLayout

class HandImageWithTextListView : LinearLayout{

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
        val horizontalParams = LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT
        )
        horizontalParams.setMargins(0, 0, 0, 0)
        this.gravity = Gravity.CENTER
        this.layoutParams = horizontalParams
    }

    fun setHandImageWithText(context: Context, handIds: List<Int>, agari: Int?) {
        removeAllViews()
        handIds.forEachIndexed {idx, imgId ->
            val view = HandImageWithTextView(context)
            if (idx == agari) view.setText("アガリ")
            view.setHandImage(imgId)
            addView(view)
        }
    }

    override fun addView(child: View?) {
        super.addView(child)
    }
}