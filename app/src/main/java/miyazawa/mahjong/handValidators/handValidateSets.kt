package miyazawa.mahjong.handValidators
import miyazawa.mahjong.model.HandType
import miyazawa.mahjong.model.HandType.*
import miyazawa.mahjong.model.HandTypeValidator

private fun MutableList<HandTypeValidator>.addHandNumber(response: HandTypeValidator?): Boolean {
    if (response != null) {
        this.add(response)
        return true
    }
    return false
}

/*
 * 指定されたリストの役の成立を判定して返す。
 */
private fun addHands(agari: Pair<Int, Int>, opened: List<List<Int>>, hidden: List<List<Int>>,
                     kanOpened: List<Int>, kanHidden: List<Int>, isTsumo: Boolean,
                     tileNumList: List<List<Int>>, hands: List<HandType>): List<HandTypeValidator> {
    val handList = mutableListOf<HandTypeValidator>()
    hands.forEach{
        handList.addHandNumber(it.validate(agari, opened, hidden, kanOpened, kanHidden, isTsumo, tileNumList))
    }
    return handList
}
private val valueHands = listOf(EAST, SOUTH, WEST, NORTH, WHITE, GREEN, RED)
//private val tsumoRelatedYakumans = listOf(YONNANKOU)
val agariRelatedYakumans = listOf(YONNANTANKI, PURECHUREN, YONNANKOU, CHUREN)
private val agariRelatedHands = listOf(PINHU, SANNANKOU)
// 国士無双は抜き
val yakumanHands = listOf(
    CHINROU, SUSHIHOU, DAISANGEN, SHOUSUSHI, ALLVALUES, ALLGREEN, YONKANTSU)
private val sevenPairsHands = listOf(HONNITSU, HONROU, CHINNITSU, ALLVALUES)
val kokushiHands = listOf(PUREKOKUSHI, KOKUSHI)
private val additionals = listOf(RIICH, DOUBLE, HAITEI, HOUTEI, RINSHAN, CHANKAN, DORA, IPPATSU)
private val otherHands = listOf(PECO1, PECO2, CHANTA, ITTSU, SANSHOKU, DOUKOU, SANKANTSU,
    TOITOI, SHOUSANGEN, HONROU, JUNCHAN, HONNITSU, CHINNITSU,
    TANNYAO, TSUMO) + valueHands


fun sevenPairs(agari: Pair<Int, Int>, opened: List<List<Int>>, hidden: List<List<Int>>,
               kanOpened: List<Int>, kanHidden: List<Int>, isTsumo: Boolean,
               tileNumList: List<List<Int>> ): List<HandTypeValidator> {
    val res = addHands(agari, opened, hidden, kanOpened, kanHidden, isTsumo, tileNumList,
        sevenPairsHands).toMutableList()
    res.add(SEVENPAIRS)
    return res
}

fun additionals(agari: Pair<Int, Int>, opened: List<List<Int>>, hidden: List<List<Int>>,
            kanOpened: List<Int>, kanHidden: List<Int>, isTsumo: Boolean,
            tileNumList: List<List<Int>> ): List<HandTypeValidator> {
    return addHands(agari, opened, hidden, kanOpened, kanHidden, isTsumo, tileNumList,
        additionals)
}

fun kokushi(agari: Pair<Int, Int>, opened: List<List<Int>>, hidden: List<List<Int>>,
            kanOpened: List<Int>, kanHidden: List<Int>, isTsumo: Boolean,
            tileNumList: List<List<Int>> ): List<HandTypeValidator> {
    return addHands(agari, opened, hidden, kanOpened, kanHidden, isTsumo, tileNumList,
        kokushiHands)
}

fun yakuman(agari: Pair<Int, Int>, opened: List<List<Int>>, hidden: List<List<Int>>,
            kanOpened: List<Int>, kanHidden: List<Int>, isTsumo: Boolean,
            tileNumList: List<List<Int>> ): List<HandTypeValidator> {

    return addHands(agari, opened, hidden, kanOpened, kanHidden, isTsumo, tileNumList,
        yakumanHands)
}

//fun tsumoRelatedYakuman(agari: Pair<Int, Int>, opened: List<List<Int>>, hidden: List<List<Int>>,
//                        kanOpened: List<Int>, kanHidden: List<Int>, isTsumo: Boolean,
//                        tileNumList: List<List<Int>> ): List<Int> {
//
//    return addHands(agari, opened, hidden, kanOpened, kanHidden, isTsumo, tileNumList,
//        tsumoRelatedYakumans)
//}

fun agariRelatedYakuman(agari: Pair<Int, Int>, opened: List<List<Int>>, hidden: List<List<Int>>,
            kanOpened: List<Int>, kanHidden: List<Int>, isTsumo: Boolean,
            tileNumList: List<List<Int>> ): List<HandTypeValidator> {

    return addHands(agari, opened, hidden, kanOpened, kanHidden, isTsumo, tileNumList,
        agariRelatedYakumans)
}

fun agariRelatedHand(agari: Pair<Int, Int>, opened: List<List<Int>>, hidden: List<List<Int>>,
                        kanOpened: List<Int>, kanHidden: List<Int>, isTsumo: Boolean,
                        tileNumList: List<List<Int>> ): List<HandTypeValidator> {

    return addHands(agari, opened, hidden, kanOpened, kanHidden, isTsumo, tileNumList,
        agariRelatedHands)
}

fun others(agari: Pair<Int, Int>, opened: List<List<Int>>, hidden: List<List<Int>>,
           kanOpened: List<Int>, kanHidden: List<Int>, isTsumo: Boolean,
           tileNumList: List<List<Int>> ): List<HandTypeValidator> {

    return addHands(agari, opened, hidden, kanOpened, kanHidden, isTsumo, tileNumList,
        otherHands)
}

fun values(agari: Pair<Int, Int>, opened: List<List<Int>>, hidden: List<List<Int>>,
           kanOpened: List<Int>, kanHidden: List<Int>, isTsumo: Boolean,
           tileNumList: List<List<Int>> ): List<HandTypeValidator> {

    return addHands(agari, opened, hidden, kanOpened, kanHidden, isTsumo, tileNumList,
        valueHands)
}
