package miyazawa.mahjong.model

import miyazawa.mahjong.handValidators.*
import miyazawa.mahjong.utils.*
import miyazawa.mahjong.variables.scores.calcRonScore
import miyazawa.mahjong.variables.scores.calcTsumoScore
import okhttp3.internal.toImmutableList

class NormalHand(
    override val agari: Int,
    _opened: MutableList<MutableList<Int>>,
    _hidden: MutableList<MutableList<Int>>,
    _kanOpened: MutableList<Int>,
    _kanHidden: MutableList<Int>
): HandCandidate {
    override var subScoreRon: Int = 0
    override var subScoreTsumo: Int = 0
    override var handsTsumo: MutableList<HandTypeValidator> = mutableListOf()
    override var handsRon: MutableList<HandTypeValidator> = mutableListOf()
    val opened: List<List<Int>>
    val hidden: List<List<Int>>
    val kanOpened: List<Int>
    val kanHidden: List<Int>
    val tileNumList: List<List<Int>>
    val isMenzen: Boolean
    val agariCandidate:List<Pair<Int, Int>>
    var ronAgariPlace: Pair<Int, Int> = Pair(0, 0)
    var tsumoAgariPlace: Pair<Int, Int> = Pair(0, 0)
    init {
        opened = _opened.copy().toList()
        hidden = _hidden.copy().toList()
        kanOpened = _kanOpened.copy().toList()
        kanHidden = _kanHidden.copy().toList()
        val tempNumList = mutableListOf<MutableList<Int>>()
        isMenzen = opened.size + kanOpened.size == 0
        for (i in 0.until(4)) {
            tempNumList.add(mutableListOf<Int>(0,0,0,0,0,0,0,0,0))
        }
        opened.forEach{
            it.forEach{
                tempNumList[it/9][it%9] += 1
            }
        }
        hidden.forEach{
            it.forEach{
                tempNumList[it/9][it%9] += 1
            }
        }
        kanHidden.forEach{
            tempNumList[it/9][it%9] += 4
        }
        kanOpened.forEach{
            tempNumList[it/9][it%9] += 4
        }
        tileNumList = tempNumList
        var tmpAgariCandidate = mutableListOf<Pair<Int, Int>>()
        // 和了牌の面子構成候補を列挙
        hidden.forEachIndexed { i, it ->
            if(!isShuntsu(it) && it[0] == agari) {
                tmpAgariCandidate.add(Pair(i, 0))
                return@forEachIndexed
            }
            it.forEachIndexed { j, it ->
                if (it == agari) {
                    tmpAgariCandidate.add(Pair(i, j))
                }
            }
        }
        agariCandidate = tmpAgariCandidate.toList()
        calculateScore()
    }

    private fun calculateScore() {
        // 自摸ロンにかかわらず役満であるもののチェック
        val tmpScoreRon = yakuman(Pair(0, 0), opened, hidden, kanOpened, kanHidden,
            false, tileNumList).toMutableList()
        val tmpScoreTsumo = tmpScoreRon.copy()
        var tmp: MutableList<HandTypeValidator>
        var ronDetermined = false
        var tsumoDetermined = false
        if (tmpScoreRon.size != 0) {
            handsRon = tmpScoreRon
            ronAgariPlace = agariCandidate[0]
            ronDetermined = true
        }
        if (tmpScoreTsumo.size != 0) {
            handsTsumo = tmpScoreTsumo
            tsumoAgariPlace = agariCandidate[0]
            tsumoDetermined = true
        }
        print(agariCandidate.toString())
        if (!ronDetermined){
            agariCandidate.forEach{
                println("ron")
                determineAgariPlace(false)
            }
        }
        if (!tsumoDetermined){
            agariCandidate.forEach{
                determineAgariPlace(true)
            }
        }
    }

    fun agariPlace(isTsumo: Boolean) : Pair<Int, Int> {
        return if (isTsumo) tsumoAgariPlace else ronAgariPlace
    }

    private fun determineAgariPlace(isTsumo: Boolean) {
        println("is tsumo: $isTsumo")
        val commomHand = others(Pair(0, 0), opened, hidden, kanOpened, kanHidden,
            isTsumo, tileNumList)
        println("commonHand: $commomHand")
        var maxScore = 0
        var maxSubScore = 0
        agariCandidate.forEach {
            var tmp = agariRelatedYakuman(it, opened, hidden, kanOpened, kanHidden,
                        isTsumo, tileNumList).toMutableList()
            println("agari related yakuman: $tmp")
            if (tmp.size != 0) {
                if (isTsumo) {
                    handsTsumo = tmp
                }else{
                    handsRon = tmp
                }
                return
            }
            tmp = agariRelatedHand(it, opened, hidden, kanOpened, kanHidden,
                        isTsumo, tileNumList).toMutableList()
            println("agariRelated: ${tmp.toString()}")
            val tmpScore = calcHandScore(tmp + commomHand)
            var tmpSubScore = 0
            if (tmpScore <= 4) {
                tmpSubScore = calcSubScore(it, isTsumo, HandType.PINHU in tmp)
            }
            println("tmp score: $tmpScore")
            if (tmpScore > maxScore || tmpScore == maxScore && tmpSubScore > maxSubScore) {
                maxScore = tmpScore
                maxSubScore = tmpSubScore
                if (isTsumo) {
                    handsTsumo = (commomHand + tmp).toMutableList().copy()
                    subScoreTsumo = tmpSubScore
                    tsumoAgariPlace = it
                } else {
                    handsRon = (commomHand + tmp).toMutableList().copy()
                    ronAgariPlace = it
                    subScoreRon = tmpSubScore
                }
            }
        }

    }

    private fun withAdditionalHands(hands: MutableList<HandTypeValidator>, isTsumo: Boolean) : MutableList<HandTypeValidator> {
        hands.forEach {
            if (it in agariRelatedYakumans || it in yakumanHands) {
                return hands
            }
        }
        return (hands + additionals(ronAgariPlace, opened, hidden, kanOpened, kanHidden, isTsumo, tileNumList)).toMutableList()
    }

    override fun ronScore(): Int {
        val subScore = calcSubScore(ronAgariPlace, false, false)
        val score = calcHandScore(withAdditionalHands(handsRon, false))
        println("ron score: ${score}, sub score: ${subScore}")
        return calcRonScore(score, subScore)
    }

    override fun handCountRon(): Int {
        return calcHandScore(withAdditionalHands(handsRon, false))
    }

    override fun handCountTsumo(): Int {
        return calcHandScore(withAdditionalHands(handsTsumo, true))
    }

    override fun tsumoScore(): Pair<Int, Int> {
        val subScore = calcSubScore(tsumoAgariPlace, true, HandType.PINHU in handsRon)
        var score = calcHandScore(withAdditionalHands(handsTsumo, true))
//        if (isMenzen && score > 0) {
//            score += 1
//        }
        println("tsumo score: ${score}, sub score: ${subScore}")
        return calcTsumoScore(score, subScore)
    }

    private fun calcHandScore(hand: List<HandTypeValidator>): Int {
       return hand.fold(initial = 0) {tmp, value -> tmp + value.score(isMenzen)}
    }

    override fun handNameRon(): List<Pair<String, Int>> {
        return mutableListOf<Pair<String, Int>>().apply {
           withAdditionalHands(handsRon, false).forEach {
               if (it.score(isMenzen) != 0) {
                   this.add(Pair(it.handName, it.score(isMenzen)))
               }
           }
        }
    }

    override fun handNameTsumo(): List<Pair<String, Int>> {
        return mutableListOf<Pair<String, Int>>().apply {
            withAdditionalHands(handsTsumo, true).forEach {
                if (it.score(isMenzen) != 0) {
                    this.add(Pair(it.handName, it.score(isMenzen)))
                }
            }
        }
    }

    /*
     * 符計算
     */
    private fun calcSubScore(agari: Pair<Int, Int>, isTsumo: Boolean, isPinhu: Boolean): Int {
        fun addSubScore(tile: Int, score: Int) : Int =
            when(tile) {
                in Constants.YAOCHU_NUMBERS -> score * 2
                else -> score
            }
        if (isPinhu && isTsumo) {
            return 20
        }
        var subScore = 20
        if (!isShuntsu(hidden[agari.first])
            || agari.second == 1
            || agari.second == 0 && hidden[agari.first][agari.second]%9 == 6
            || agari.second == 2 && hidden[agari.first][agari.second]%9 == 2) {
            subScore += 2
        }
        if (isTsumo) {
            subScore += 2
        } else if(isMenzen) {
            subScore += 10
        }
        opened.forEach {
            if (isKoutsu(it)) {
                subScore += addSubScore(it[0], 2)
            }
        }
        hidden.forEachIndexed {i, it ->
            if (isKoutsu(it)) {
                if (isTsumo || agari.first != i) {
                    subScore += addSubScore(it[0], 4)
                } else {
                    subScore += addSubScore(it[0], 2)
                }
            } else if (isToistu(it)) {
                if (isMyValue(it[0])) {
                    subScore += 2
                }
                if (isFieldValue(it[0])) {
                    subScore += 2
                }
                if (isPFC(it[0])) {
                    subScore += 2
                }
            }
        }
        kanOpened.forEach {
            subScore += addSubScore(it, 8)
        }
        kanHidden.forEach {
            subScore += addSubScore(it, 16)
        }
        return subScore
    }

}