package miyazawa.mahjong

import android.content.Context
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter

class HandImageAdapter(private val context: Context) : BaseAdapter() {
    var handIds: List<Int> = emptyList()

    override fun getCount(): Int = handIds.size

    override fun getItem(position: Int): Any = handIds[position]

    override fun getItemId(position: Int): Long = handIds[position].toLong()

    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View =
        ((convertView as? HandImageView) ?: HandImageView(context)).apply {
            setHandImage(handIds[position])
        }
}