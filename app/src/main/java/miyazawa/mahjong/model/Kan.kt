package miyazawa.mahjong.model

import java.io.Serializable

data class Kan (
    val opened: MutableList<String> = mutableListOf(),
    val hidden: MutableList<String> = mutableListOf()
) : Serializable {
    fun size(): Int = opened.size + hidden.size
    override fun toString(): String {
       return "openedKan : ${opened.toString()}\n" +
               "hiddenKan : ${hidden.toString()}\n"
    }
}
