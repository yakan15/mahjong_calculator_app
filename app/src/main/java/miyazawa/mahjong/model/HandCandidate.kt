package miyazawa.mahjong.model

import java.io.Serializable

interface HandCandidate : Serializable {
    val agari: Int
    var handsTsumo: MutableList<HandTypeValidator>
    var handsRon: MutableList<HandTypeValidator>
    var subScoreTsumo: Int
    var subScoreRon: Int
    fun ronScore(): Int
    fun tsumoScore(): Pair<Int, Int>
    fun handCountRon(): Int
    fun handCountTsumo(): Int
    fun handNameRon(): List<Pair<String, Int>>
    fun handNameTsumo(): List<Pair<String, Int>>
//    var isTsumo: Boolean
//    fun evaluate(): Boolean
}


