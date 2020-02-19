package miyazawa.mahjong.utils

import android.util.Log
import miyazawa.mahjong.R
import miyazawa.mahjong.variables.Variables.field
import miyazawa.mahjong.variables.Variables.me
import java.lang.Exception
import java.lang.reflect.Field

fun <T> MutableList<T>.copy() : MutableList<T> {
    val arr = mutableListOf<T>()
    this.forEach {
       arr.add(it)
    }
    return arr
}
/*
 * 手牌分割前の面前部分 [b] に対し、和了牌 [a] を追加してソートしたリストを返却する。
 *
 *
 */
fun agariAdded(agari: Int, hiddenHands: MutableList<Int>) : MutableList<Int> {
    val res = hiddenHands.copy()
    res.add(agari)
    res.sort()
    return res
}
fun isValueHead(tile: Int) : Boolean = tile in 27..29 || tile == field.tileId || tile == me.tileId

fun isMyValue(tile: Int) : Boolean = tile == me.tileId

fun isFieldValue(tile: Int) : Boolean = tile == me.tileId

fun isPFC(tile: Int) : Boolean = tile in 27..29

fun isValues(tile: Int) : Boolean  = tile >= 27

fun isMs(tile: Int) : Boolean = tile <= 8

fun isPs(tile: Int) : Boolean = tile in 9..17

fun isSs(tile: Int) : Boolean = tile in 18..26

fun isSameNumbers(a: Int, b: Int): Boolean {
    return isMs(a) && isMs(b)
            || isPs(a) && isPs(b)
            || isSs(a) && isSs(b)
}

fun isShuntsu(hand: List<Int>): Boolean {
   return hand.size == 3 && hand[0] + 1 == hand[1] && hand[1] + 1 == hand[2]
}

fun isKoutsu(hand: List<Int>): Boolean {
    return hand.size == 3 && hand[0] == hand[1] && hand[1]  == hand[2]
}

fun isToistu(hand: List<Int>): Boolean {
    return hand.size == 2 && hand[0] == hand[1]
}

fun <T:Any> T.logd(message: String) {
    Log.d(this::class.java.simpleName, message)
}

fun <T:Any> T.loge(message: String) {
    Log.d(this::class.java.simpleName, message)
}

fun getFieldId(name: String) : Int? {
    try {
        val idField: Field = R.id::class.java.getDeclaredField(name)
        return idField.getInt(idField)
    } catch (e: Exception) {
        return null
    }
}
