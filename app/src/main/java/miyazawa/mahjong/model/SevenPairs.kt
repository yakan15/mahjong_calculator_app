package miyazawa.mahjong.model

import miyazawa.mahjong.handValidators.additionals
import miyazawa.mahjong.handValidators.agariRelatedYakumans
import miyazawa.mahjong.utils.copy
import miyazawa.mahjong.handValidators.sevenPairs
import miyazawa.mahjong.handValidators.yakumanHands
import miyazawa.mahjong.variables.scores.calcRonScore
import miyazawa.mahjong.variables.scores.calcTsumoScore

class SevenPairs constructor(
    override val agari: Int,
//    override var isTsumo: Boolean,
    _hiddenHands: MutableList<Int>
) : HandCandidate {
    private val hiddenHands: MutableList<Int>
    override lateinit var handsTsumo: MutableList<HandTypeValidator>
    override lateinit var handsRon: MutableList<HandTypeValidator>
    override var subScoreTsumo: Int = 25
    override var subScoreRon: Int = 25
    val tileNumList: List<List<Int>>
    init {
        require(validate(agari, _hiddenHands))
        hiddenHands = _hiddenHands.copy()
        var tempNumList = mutableListOf<MutableList<Int>>()
        for (i in 0.until(4)) {
            tempNumList.add(mutableListOf<Int>(0,0,0,0,0,0,0,0,0))
        }
        hiddenHands.forEach{
            tempNumList[it/9][it%9] += 4
        }
        tileNumList = tempNumList
        handsRon = sevenPairs(Pair(0, 0), listOf(), listOf(), listOf(), listOf(), false, tileNumList).toMutableList()
        handsTsumo = (handsRon + HandType.TSUMO).toMutableList()
    }

    private fun withAdditionalHands(hands: MutableList<HandTypeValidator>) : MutableList<HandTypeValidator> {
        hands.forEach {
            if (it in agariRelatedYakumans || it in yakumanHands) {
                return hands
            }
        }
        return (hands + additionals(Pair(0, 0), listOf(), listOf(), listOf(), listOf(), false, tileNumList)).toMutableList()
    }

    override fun handNameRon(): List<Pair<String, Int>> {
        return mutableListOf<Pair<String, Int>>().apply {
            withAdditionalHands(handsRon).forEach {
                if (it.score(true) != 0) {
                    this.add(Pair(it.handName, it.score(true)))
                }
            }
        }
    }

    override fun handNameTsumo(): List<Pair<String, Int>> {
        return mutableListOf<Pair<String, Int>>().apply {
            withAdditionalHands(handsTsumo).forEach {
                if (it.score(true) != 0) {
                    this.add(Pair(it.handName, it.score(true)))
                }
            }
        }
    }

    private fun calcHandScore(hand: List<HandTypeValidator>): Int {
        return hand.fold(initial = 0) {tmp, value -> tmp + value.score(true)}
    }

    override fun handCountRon(): Int {
        return calcHandScore(withAdditionalHands(handsRon))
    }

    override fun handCountTsumo(): Int {
        return calcHandScore(withAdditionalHands(handsTsumo))
    }

    override fun ronScore(): Int {
        val score = calcHandScore(withAdditionalHands(handsRon))
        return calcRonScore(score, 25)
    }

    override fun tsumoScore(): Pair<Int, Int> {
        val score = calcHandScore(withAdditionalHands(handsTsumo))
        return calcTsumoScore(score, 25)
    }

    companion object {
        fun validate(agari: Int, hiddenHands: MutableList<Int>, enableFour: Boolean = false): Boolean {
            val hand = hiddenHands.copy()
            val checked: MutableSet<Int> = mutableSetOf()
            hand.add(agari)
            if (hiddenHands.size != 14) {
                return false
            }
            for (i in  0..6) {
                if (hand[2*i] != hand[2*i+1]) {
                    return false
                }
                if (!enableFour) {
                    if (checked.contains(hand[2*i])) {
                        return false
                    }
                    checked.plus(hand[2*i])
                }
            }
            return true
        }
    }
}