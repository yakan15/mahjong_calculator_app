package miyazawa.mahjong.model

import miyazawa.mahjong.handValidators.agariRelatedYakumans
import miyazawa.mahjong.handValidators.tsumo
import miyazawa.mahjong.handValidators.yakumanHands
import miyazawa.mahjong.model.Constants.YAOCHU_NUMBERS
import miyazawa.mahjong.utils.*
import miyazawa.mahjong.variables.Variables

class SubScoreDisplayHelper(private val hand: NormalHand, private val isTsumo : Boolean) {
    private val isYakuman : Boolean
    private val isPinhuTsumo : Boolean
    private var subScore : Int? = null
    private val isMenzen : Boolean
    private val agariState : AGARI
    val agariPlace : Pair<Int, Int>
    var handList: List<Triple<List<Int>, Int, STATUS>> = listOf()

    init {
        val handValues = when(isTsumo) {
            true -> hand.handsTsumo
            false -> hand.handsRon
        }
        isMenzen = hand.isMenzen
        isYakuman = checkYakuman(handValues)
        agariState = setAgariState()
        agariPlace = hand.agariPlace(isTsumo)
        isPinhuTsumo = (HandType.TSUMO in handValues
                && HandType.PINHU in handValues)
        calculateHandSubScore()
    }

    fun createResultTextList() : List<Pair<String, Int>> {
        if (isYakuman) return listOf()
        if (isPinhuTsumo) return listOf("平和ツモ" to 20)
        var resultList = mutableListOf<Pair<String, Int>>()
        resultList.add("基本符" to 20)
        if (isTsumo) resultList.add("ツモ" to 2)
        if (isMenzen && !isTsumo) resultList.add("面前ロン" to 10)
        if (agariState != AGARI.RYANMEN) resultList.add(agariState.agariName to 2)
        resultList.add("手牌" to handList.fold(initial = 0) {acc, triple -> acc + triple.second })
        return resultList
    }

    fun resultSum() : Int? = subScore

    private fun setAgariState(): AGARI {
        val agari = hand.agariPlace(isTsumo)
        if (isKoutsu(hand.hidden[agari.first])) {
            return AGARI.SHANPON
        } else if (isToistu(hand.hidden[agari.first])) {
            return AGARI.TANKI
        } else if (agari.second == 1) {
            return AGARI.KANCHAN
        } else if (agari.second == 0 && hand.hidden[agari.first][agari.second] % 9 == 6
            || agari.second == 2 && hand.hidden[agari.first][agari.second] % 9 == 2) {
            return AGARI.PENCHAN
        }
        return AGARI.RYANMEN
    }

    private fun checkYakuman(handValues: List<HandTypeValidator>) : Boolean {
        val yakuman = yakumanHands + agariRelatedYakumans
        yakuman.forEach {
            if (it in handValues) {
                return true
            }
        }
        return false
    }

    private fun calculateHandSubScore() {

        fun koutsuScore(tileId: Int, isOpened: Boolean) : Int =
            when(tileId) {
                in YAOCHU_NUMBERS -> if (isOpened) 4 else 8
                else -> if (isOpened) 2 else 4
            }

        fun kantsuScore(tileId: Int, isOpened: Boolean) : Int =
            koutsuScore(tileId, isOpened) * 4

        fun handSubScore(lst: List<Int>, isOpened: Boolean) : Int {
            if (isShuntsu(lst)) {
                return 0
            }
            if (isToistu(lst)) {
                if (!isValueHead(lst[0])) {
                    return 0
                } else if(Variables.me.tileId == lst[0] && Variables.field.tileId == lst[0]) {
                    return 4
                }
                return 2
            }
            if (lst.size == 4) {
                return kantsuScore(lst[0], isOpened)
            }
            return koutsuScore(lst[0], isOpened)
        }

        var _subScore = 20
//        if (isYakuman) {
//            return
//        } else if(isPinhuTsumo) {
//            subScore = 20
//            return
//        }
        if (!isTsumo && isMenzen) {
            _subScore += 10
        }
        if (isTsumo) {
            _subScore += 2
        }
        if (agariState != AGARI.RYANMEN) {
            _subScore += 2
        }
        var handList = mutableListOf<Triple<List<Int>, Int, STATUS>>()
        hand.hidden.forEachIndexed { idx, it ->
                handList.add(
                    Triple(
                    it,
                    handSubScore(it, !isTsumo && idx == agariPlace.first),
                    STATUS.MENZEN)
                )
        }
        hand.kanHidden.forEach {
            val tmp = listOf(it, 34, 34, it)
            handList.add(Triple(
                tmp,
                handSubScore(tmp, false),
                STATUS.MENZEN))
        }
        hand.opened.forEach {
            handList.add(Triple(
                it,
                handSubScore(it, true),
                STATUS.NAKI
            ))
        }
        hand.kanOpened.forEach {
            val tmp = listOf(it, it, it, it)
            handList.add(Triple(
                tmp,
                handSubScore(tmp, true),
                STATUS.NAKI))
        }
        this.handList = handList.toList()
        if (isPinhuTsumo) {
            this.subScore = 20
        } else if(isYakuman) {
            this.subScore = null
        } else {
            this.subScore = handList.fold(initial = _subScore) {
                    acc, triple ->  acc + triple.second
            }
        }
    }

    companion object {
        enum class STATUS {
            MENZEN, NAKI
        }

        interface agariName {
            val agariName : String
        }

        enum class AGARI : agariName {
            RYANMEN {
                override val agariName: String = "リャンメン待ち"
            },
            SHANPON {
                override val agariName: String = "シャンポン待ち"
            },
            KANCHAN{
                override val agariName: String = "カンチャン待ち"
            },
            PENCHAN {
                override val agariName: String = "ペンチャン待ち"
            },
            TANKI {
                override val agariName: String = "タンキ待ち"
            }
        }
    }
}