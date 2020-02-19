package miyazawa.mahjong.model

import miyazawa.mahjong.utils.copy
import miyazawa.mahjong.model.HandType.KOKUSHI
import miyazawa.mahjong.model.HandType.PUREKOKUSHI
import miyazawa.mahjong.variables.scores.calcRonScore
import miyazawa.mahjong.variables.scores.calcTsumoScore

class Kokushi constructor(
    override val agari: Int,
    _hiddenHands: MutableList<Int>
    ) : HandCandidate {
    override var handsTsumo: MutableList<HandTypeValidator> = mutableListOf()
    override var handsRon: MutableList<HandTypeValidator> = mutableListOf()
    override var subScoreTsumo: Int = 0
    override var subScoreRon: Int = 0
    init {
        require(validate(agari, _hiddenHands))
        if (_hiddenHands.count({it == agari}) == 2) {
            handsTsumo.add(PUREKOKUSHI)
            handsRon.add(PUREKOKUSHI)
        } else {
            handsTsumo.add(KOKUSHI)
            handsRon.add(KOKUSHI)
        }
    }

    override fun ronScore(): Int = calcRonScore(handsRon[0].score(true), 0)
    override fun tsumoScore(): Pair<Int, Int> = calcTsumoScore(handsRon[0].score(true), 0)

    private fun calcHandScore(hand: List<HandTypeValidator>): Int {
        return hand.fold(initial = 0) {tmp, value -> tmp + value.score(true)}
    }

    override fun handCountRon(): Int {
        return calcHandScore(handsRon)
    }

    override fun handCountTsumo(): Int {
        return calcHandScore(handsTsumo)
    }

    override fun handNameRon(): List<Pair<String, Int>> {
        return mutableListOf<Pair<String, Int>>().apply {
            handsRon.forEach {
                if (it.score(true) != 0) {
                    this.add(Pair(it.handName, it.score(true)))
                }
            }
        }
    }

    override fun handNameTsumo(): List<Pair<String, Int>> {
        return mutableListOf<Pair<String, Int>>().apply {
            handsTsumo.forEach {
                if (it.score(true) != 0) {
                    this.add(Pair(it.handName, it.score(true)))
                }
            }
        }
    }


    companion object {
        fun validate(agari: Int, hiddenHands: MutableList<Int>): Boolean {
            var hand = hiddenHands.copy()
            if (hand.size != 14) {
                return false
            }
            for (i in 0.until(Constants.NUM_TILES)) {
                if (i in Constants.YAOCHU_NUMBERS && !(i in hand)
                    || !(i in Constants.YAOCHU_NUMBERS) && i in hand) {
                    return false
                }
            }
            return true
        }
    }
}