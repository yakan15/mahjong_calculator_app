package miyazawa.mahjong

import android.content.Context
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter

class HandDetailAdapter(private val context : Context) : BaseAdapter() {
    var handInfo: List<Pair<String, Int>> = emptyList()

    override fun getCount(): Int = handInfo.size

    override fun getItem(position: Int): Any = handInfo[position]

    override fun getItemId(position: Int): Long = 0

    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View =
        ((convertView as? HandDetailView) ?: HandDetailView(context)).apply {
            setText(handInfo[position].first, handInfo[position].second)
        }
}