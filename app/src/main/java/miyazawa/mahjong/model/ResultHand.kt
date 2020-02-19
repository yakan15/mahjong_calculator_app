package miyazawa.mahjong.model

import java.io.Serializable

class ResultHand (
    val opened: MutableList<MutableList<String>> = mutableListOf(),
    val hidden: MutableList<String> = mutableListOf(),
    val agari: String? = null,
    val kan: Kan? = null
) : Serializable {
    override fun toString(): String {
        return "agari : ${agari}\n" +
                "opened : ${opened.toString()}\n" +
                "hidden : ${hidden.toString()}\n" +
                 kan?.toString()
    }
}


