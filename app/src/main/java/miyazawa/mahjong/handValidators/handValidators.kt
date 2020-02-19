package miyazawa.mahjong.handValidators

import miyazawa.mahjong.model.Constants
import miyazawa.mahjong.utils.*
import miyazawa.mahjong.variables.Variables

// テンプレ

fun pinhu(agari: Pair<Int, Int>, opened: List<List<Int>>, hidden: List<List<Int>>,
          kanOpened: List<Int>, kanHidden: List<Int>, isTsumo: Boolean,
          tileNumList: List<List<Int>> ): Boolean {
   if (hidden.size != 5) {
       return false
   }
    if (agari.second == 1
        || isToistu(hidden[agari.first])
        || agari.second == 2 && hidden[agari.first][agari.second] % 9 == 2
        || agari.second == 0 && hidden[agari.first][agari.second] % 9 == 6) {
        return false
    }
   hidden.forEach{
       if (isKoutsu(it) || isToistu(it) && isValueHead( it[0] )) {
           return false
       }
   }
    return true
}

fun tannyao(agari: Pair<Int, Int>, opened: List<List<Int>>, hidden: List<List<Int>>,
          kanOpened: List<Int>, kanHidden: List<Int>, isTsumo: Boolean,
            tileNumList: List<List<Int>> ): Boolean {
    for (i in 0.until(3)) {
        if (tileNumList[i][0] + tileNumList[i][8] != 0) {
            return false
        }
    }
    if (tileNumList[3].sum() != 0) {
        return false
    }
    return true
}

/*
 * 役牌チェック
 */
fun values(agari: Pair<Int, Int>, opened: List<List<Int>>, hidden: List<List<Int>>,
          kanOpened: List<Int>, kanHidden: List<Int>, isTsumo: Boolean,
           tileNumList: List<List<Int>> , typeId: Int): Boolean {
    return tileNumList[typeId/9][typeId%9] >= 3
}

/*
 * 一盃口
 */
fun peco1(agari: Pair<Int, Int>, opened: List<List<Int>>, hidden: List<List<Int>>,
           kanOpened: List<Int>, kanHidden: List<Int>, isTsumo: Boolean,
          tileNumList: List<List<Int>> ): Boolean {
    if (hidden.size != 5) {
        return false
    }
    if (peco2( agari, opened, hidden, kanOpened, kanHidden, isTsumo, tileNumList ) ) {
        return false
    }
    val set: MutableSet<Int> = mutableSetOf()
    hidden.forEach{
        if (isShuntsu(it)) {
            if (set.contains(it[0])) {
                return true
            }
            set.add(it[0])
        }
    }
    return false
}

fun junchan(agari: Pair<Int, Int>, opened: List<List<Int>>, hidden: List<List<Int>>,
          kanOpened: List<Int>, kanHidden: List<Int>, isTsumo: Boolean,
            tileNumList: List<List<Int>> ): Boolean {
    val filterS = arrayOf(0, 6)
    val filterK = arrayOf(0, 8)
    opened.forEach{
        if(!(!isValues(it[0]) && (
                    it[0]%9 in filterS && isShuntsu(it)
                            || it[0]%9 in filterK && !isShuntsu(it)))) {
            return false
        }
    }
    hidden.forEach{
        if(!(!isValues(it[0]) && (
                    it[0]%9 in filterS && isShuntsu(it)
                            || it[0]%9 in filterK && !isShuntsu(it)))) {
            return false
        }
    }
    kanHidden.forEach{
        if(!(it%9 in filterK)) {
            return false
        }
    }
    kanOpened.forEach{
        if(!(it in filterK)) {
            return false
        }
    }
    return true
}

fun chanta(agari: Pair<Int, Int>, opened: List<List<Int>>, hidden: List<List<Int>>,
            kanOpened: List<Int>, kanHidden: List<Int>, isTsumo: Boolean,
           tileNumList: List<List<Int>> ): Boolean {
    if (junchan( agari, opened, hidden, kanOpened, kanHidden, isTsumo, tileNumList ) ) {
        return false
    }
    if (honrou( agari, opened, hidden, kanOpened, kanHidden, isTsumo, tileNumList ) ) {
        return false
    }
    val filterS = arrayOf(0, 6)
    val filterK = arrayOf(0, 8)
    opened.forEach{
        if(!(isValues(it[0]) ||
                    it[0]%9 in filterS && isShuntsu(it)
                            || it[0]%9 in filterK && !isShuntsu(it))) {
            return false
        }
    }
    hidden.forEach{
        if(!(isValues(it[0]) ||
                    it[0]%9 in filterS && isShuntsu(it)
                    || it[0]%9 in filterK && !isShuntsu(it))) {
            return false
        }
    }
    kanHidden.forEach{
        if(!(it%9 in Constants.YAOCHU_NUMBERS)) {
            return false
        }
    }
    kanOpened.forEach{
        if(!(it%9 in Constants.YAOCHU_NUMBERS)) {
            return false
        }
    }
    return true
}

fun ittsu(agari: Pair<Int, Int>, opened: List<List<Int>>, hidden: List<List<Int>>,
           kanOpened: List<Int>, kanHidden: List<Int>, isTsumo: Boolean,
          tileNumList: List<List<Int>> ): Boolean {
    val counts = Array(3, {Array(9, {it->0})})
    opened.forEach{
        if (isShuntsu(it)) {
            counts[it[0]/9][it[0]%9] += 1
        }
    }
    hidden.forEach{
        if (isShuntsu(it)) {
            counts[it[0]/9][it[0]%9] += 1
        }
    }
    counts.forEach {
        if (it[0] != 0 && it[3] != 0 && it[6] != 0) {
            return true
        }
    }
    return false
}

fun sanshoku(agari: Pair<Int, Int>, opened: List<List<Int>>, hidden: List<List<Int>>,
          kanOpened: List<Int>, kanHidden: List<Int>, isTsumo: Boolean,
             tileNumList: List<List<Int>> ): Boolean {
    val counts = Array(3, {Array(9, {it->0})})
    opened.forEach{
        if (isShuntsu(it)) {
            counts[it[0]/9][it[0]%9] += 1
        }
    }
    hidden.forEach{
        if (isShuntsu(it)) {
            counts[it[0]/9][it[0]%9] += 1
        }
    }
    for (i in 0.until(9)) {
       if (counts[0][i] != 0 && counts[1][i] != 0 && counts[2][i] != 0) {
           return true
       }
    }
    return false
}

fun doukou(agari: Pair<Int, Int>, opened: List<List<Int>>, hidden: List<List<Int>>,
             kanOpened: List<Int>, kanHidden: List<Int>, isTsumo: Boolean,
           tileNumList: List<List<Int>> ): Boolean {
    val counts = Array(4, {Array(9, {it->0})})
    opened.forEach{
        if (isKoutsu(it)) {
            counts[it[0]/9][it[0]%9] += 1
        }
    }
    hidden.forEach{
        if (isKoutsu(it)) {
            counts[it[0]/9][it[0]%9] += 1
        }
    }
    kanOpened.forEach{
        counts[it/9][it%9] += 1
    }
    kanHidden.forEach{
        counts[it/9][it%9] += 1
    }
    for (i in 0.until(9)) {
        if (counts[0][i] != 0 && counts[1][i] != 0 && counts[2][i] != 0) {
            return true
        }
    }
    return false
}

fun sankantsu(agari: Pair<Int, Int>, opened: List<List<Int>>, hidden: List<List<Int>>,
             kanOpened: List<Int>, kanHidden: List<Int>, isTsumo: Boolean,
              tileNumList: List<List<Int>> ): Boolean {
    return kanHidden.size + kanOpened.size == 3
}



fun toitoi(agari: Pair<Int, Int>, opened: List<List<Int>>, hidden: List<List<Int>>,
              kanOpened: List<Int>, kanHidden: List<Int>, isTsumo: Boolean,
           tileNumList: List<List<Int>> ): Boolean {
    opened.forEach{
        if (isShuntsu(it)) {
            return false
        }
    }
    hidden.forEach{
        if (isShuntsu(it)) {
            return false
        }
    }
    return true
}

fun sannankou(agari: Pair<Int, Int>, opened: List<List<Int>>, hidden: List<List<Int>>,
             kanOpened: List<Int>, kanHidden: List<Int>, isTsumo: Boolean,
              tileNumList: List<List<Int>> ): Boolean {
    if (yonnankou(agari, opened, hidden, kanOpened, kanHidden, isTsumo, tileNumList)) {
        return false
    }
    var count = 0
    hidden.forEachIndexed{i, it ->
        if (isKoutsu(it) && (isTsumo || agari.first != i)) {
            count += 1
        }
    }
    return count + kanHidden.size == 3
}

fun yonnankou(agari: Pair<Int, Int>, opened: List<List<Int>>, hidden: List<List<Int>>,
              kanOpened: List<Int>, kanHidden: List<Int>, isTsumo: Boolean,
              tileNumList: List<List<Int>> ): Boolean {
    if (yonnantannki(agari, opened, hidden, kanOpened, kanHidden, isTsumo, tileNumList)) {
        return false
    }
    var count = 0
    hidden.forEachIndexed{i, it ->
        if (isKoutsu(it) && (isTsumo || agari.first != i)) {
            count += 1
        }
    }
    return count + kanHidden.size == 4
}

fun daisangen(agari: Pair<Int, Int>, opened: List<List<Int>>, hidden: List<List<Int>>,
             kanOpened: List<Int>, kanHidden: List<Int>, isTsumo: Boolean,
              tileNumList: List<List<Int>> ): Boolean {
    for (i in 27..29) {
        if (tileNumList[i/9][i%9] < 3) {
            return false
        }
    }
    return true
}

fun shousangen(agari: Pair<Int, Int>, opened: List<List<Int>>, hidden: List<List<Int>>,
             kanOpened: List<Int>, kanHidden: List<Int>, isTsumo: Boolean,
               tileNumList: List<List<Int>> ): Boolean {
    if (daisangen(agari, opened,hidden, kanOpened, kanHidden, isTsumo, tileNumList)) {
        return false
    }
    var countH = 0
    var countK = 0
    for ( i in 27..29) {
        when(tileNumList[i/9][i%9]) {
            2 -> countH += 1
            3, 4 -> countK += 1
            else -> return false
        }
    }

    return countH == 1 && countK == 2
}

fun allvalues(agari: Pair<Int, Int>, opened: List<List<Int>>, hidden: List<List<Int>>,
           kanOpened: List<Int>, kanHidden: List<Int>, isTsumo: Boolean,
              tileNumList: List<List<Int>> ): Boolean {
    for (i in 0..2) {
        if (tileNumList[i].sum() != 0) {
            return false
        }
    }
    return true
}

fun honrou(agari: Pair<Int, Int>, opened: List<List<Int>>, hidden: List<List<Int>>,
             kanOpened: List<Int>, kanHidden: List<Int>, isTsumo: Boolean,
           tileNumList: List<List<Int>> ): Boolean {
    if(allvalues(agari, opened, hidden, kanHidden, kanOpened, isTsumo, tileNumList)) {
        return false
    }
    if(!honnitsu(agari, opened, hidden, kanOpened, kanHidden, isTsumo, tileNumList)) {
        return false
    }
    for (i in 0.until(3)) {
        if (tileNumList[i].slice(1..7).sum() != 0) {
            return false
        }
    }
    return true
}

fun chinnitsu(agari: Pair<Int, Int>, opened: List<List<Int>>, hidden: List<List<Int>>,
             kanOpened: List<Int>, kanHidden: List<Int>, isTsumo: Boolean,
              tileNumList: List<List<Int>> ): Boolean {
    if (allvalues(agari, opened, hidden, kanOpened, kanHidden, isTsumo, tileNumList)) {
        return false
    }
    var count = 0
    tileNumList.forEach {
       if (it.sum() != 0) {
           count += 1
       }
    }
    return count == 1
}

fun honnitsu(agari: Pair<Int, Int>, opened: List<List<Int>>, hidden: List<List<Int>>,
             kanOpened: List<Int>, kanHidden: List<Int>, isTsumo: Boolean,
             tileNumList: List<List<Int>> ): Boolean {
    if (allvalues(agari, opened, hidden, kanOpened, kanHidden, isTsumo, tileNumList)) {
        return false
    }
    if (chinnitsu(agari, opened, hidden, kanOpened, kanHidden, isTsumo, tileNumList)) {
        return false
    }
    var count = 0
    for (i in 0.until(3)) {
        if (tileNumList[i].sum() != 0) {
            count += 1
        }
    }
    return count == 1
}

fun peco2(agari: Pair<Int, Int>, opened: List<List<Int>>, hidden: List<List<Int>>,
          kanOpened: List<Int>, kanHidden: List<Int>, isTsumo: Boolean,
          tileNumList: List<List<Int>> ): Boolean {
    if (hidden.size != 5) {
        return false
    }
    val set: MutableSet<Int> = mutableSetOf()
    var count = 0
    hidden.forEach{
        if (isShuntsu(it)) {
            if (set.contains(it[0])) {
                count += 1
                if (count == 2) {
                    return true
                }
                set.remove(it[0])
            }else {
                set.add(it[0])
            }
        }
    }
    return false
}


fun yonkantsu(agari: Pair<Int, Int>, opened: List<List<Int>>, hidden: List<List<Int>>,
             kanOpened: List<Int>, kanHidden: List<Int>, isTsumo: Boolean,
              tileNumList: List<List<Int>> ): Boolean {
    return kanHidden.size + kanOpened.size == 4
}


fun chinrou(agari: Pair<Int, Int>, opened: List<List<Int>>, hidden: List<List<Int>>,
              kanOpened: List<Int>, kanHidden: List<Int>, isTsumo: Boolean,
              tileNumList: List<List<Int>> ): Boolean {
    if (!junchan(agari, opened, hidden, kanOpened, kanHidden, isTsumo, tileNumList)) {
        return false
    }
    for (i in 0.until(3)) {
        if (tileNumList[i].slice(1..7).sum() != 0) {
            return false
        }
    }
    return true
}

fun allgreen(agari: Pair<Int, Int>, opened: List<List<Int>>, hidden: List<List<Int>>,
              kanOpened: List<Int>, kanHidden: List<Int>, isTsumo: Boolean,
             tileNumList: List<List<Int>> ): Boolean {
    val greenList = listOf(19, 20, 21, 23, 25, 28)
    var count = 0
    for (i in 0..33) {
        if (i !in greenList && tileNumList[i/9][i%9] > 0) {
            return false
        }
    }
    return true
}

fun churen(agari: Pair<Int, Int>, opened: List<List<Int>>, hidden: List<List<Int>>,
             kanOpened: List<Int>, kanHidden: List<Int>, isTsumo: Boolean,
             tileNumList: List<List<Int>> ): Boolean {
    if (!chinnitsu(agari, opened, hidden, kanOpened, kanHidden, isTsumo, tileNumList)) {
        return false
    }
    if (hidden.size != 5) {
        return false
    }
    tileNumList.forEach{
        if (it.sum() == 0) {
            return@forEach
        }
        if (it.sum() != 14) {
            return false
        }
        it.forEachIndexed {i, it ->
            if ((i == 0 || i == 8) && it < 3 || it == 0) {
                return false
            }
        }
    }
    val agariNum = hidden[agari.first][agari.second]
    return !((agariNum%9 == 0 || agariNum%9 == 8) && tileNumList[agariNum/9][agariNum%9] == 4
            || tileNumList[agariNum/9][agariNum%9] == 2)
}

fun purechuren(agari: Pair<Int, Int>, opened: List<List<Int>>, hidden: List<List<Int>>,
           kanOpened: List<Int>, kanHidden: List<Int>, isTsumo: Boolean,
           tileNumList: List<List<Int>> ): Boolean {
    if (!chinnitsu(agari, opened, hidden, kanOpened, kanHidden, isTsumo, tileNumList)) {
        return false
    }
    tileNumList.forEach{
        if (it.sum() == 0) {
            return@forEach
        }
        if (it.sum() != 14) {
            return false
        }
        it.forEachIndexed {i, it ->
            if ((i == 0 || i == 8) && it < 3 || it == 0) {
                return false
            }
        }
    }
    val agariNum = hidden[agari.first][agari.second]
    return (agariNum%9 == 0 || agariNum%9 == 8) && tileNumList[agariNum/9][agariNum%9] == 4
            || tileNumList[agariNum/9][agariNum%9] == 2
}

fun yonnantannki(agari: Pair<Int, Int>, opened: List<List<Int>>, hidden: List<List<Int>>,
               kanOpened: List<Int>, kanHidden: List<Int>, isTsumo: Boolean,
               tileNumList: List<List<Int>> ): Boolean {
    var count = 0
    hidden.forEachIndexed{i, it ->
        if (isKoutsu(it) && (isTsumo || agari.first != i)) {
            count += 1
        }
    }
    if (count + kanHidden.size != 4) {
        return false
    }

    val agariNum = hidden[agari.first][agari.second]
    return tileNumList[agariNum/9][agariNum%9] == 2
}

fun sushihou(agari: Pair<Int, Int>, opened: List<List<Int>>, hidden: List<List<Int>>,
             kanOpened: List<Int>, kanHidden: List<Int>, isTsumo: Boolean,
             tileNumList: List<List<Int>> ): Boolean {
    for (i in 30..33) {
       if (tileNumList[i/9][i%9] < 3) {
           return false
       }
    }
    return true
}

fun shousushi(agari: Pair<Int, Int>, opened: List<List<Int>>, hidden: List<List<Int>>,
             kanOpened: List<Int>, kanHidden: List<Int>, isTsumo: Boolean,
             tileNumList: List<List<Int>> ): Boolean {
    if (sushihou(agari, opened, hidden, kanOpened, kanHidden, isTsumo, tileNumList)) {
        return false
    }
    var countH = 0
    var countK = 0
    for ( i in 30..33) {
        when(tileNumList[i/9][i%9]) {
            2 -> countH += 1
            3, 4 -> countK += 1
            else -> return false
        }
    }
    return countH == 1 && countK == 3
}

fun tsumo(agari: Pair<Int, Int>, opened: List<List<Int>>, hidden: List<List<Int>>,
              kanOpened: List<Int>, kanHidden: List<Int>, isTsumo: Boolean,
              tileNumList: List<List<Int>> ): Boolean {
    return isTsumo && opened.size + kanOpened.size == 0
}

fun rinshan(agari: Pair<Int, Int>, opened: List<List<Int>>, hidden: List<List<Int>>,
             kanOpened: List<Int>, kanHidden: List<Int>, isTsumo: Boolean,
             tileNumList: List<List<Int>> ): Boolean {
    return Variables.bonus == Variables.Bonus.RINSHAN && isTsumo && kanOpened.size + kanHidden.size > 0
}

fun haitei(agari: Pair<Int, Int>, opened: List<List<Int>>, hidden: List<List<Int>>,
             kanOpened: List<Int>, kanHidden: List<Int>, isTsumo: Boolean,
             tileNumList: List<List<Int>> ): Boolean {
    return Variables.bonus == Variables.Bonus.HAITEI
}

fun chankan(agari: Pair<Int, Int>, opened: List<List<Int>>, hidden: List<List<Int>>,
           kanOpened: List<Int>, kanHidden: List<Int>, isTsumo: Boolean,
           tileNumList: List<List<Int>> ): Boolean {
    return Variables.bonus == Variables.Bonus.CHANKAN && !isTsumo
}

fun dora(agari: Pair<Int, Int>, opened: List<List<Int>>, hidden: List<List<Int>>,
             kanOpened: List<Int>, kanHidden: List<Int>, isTsumo: Boolean,
             tileNumList: List<List<Int>> ): Boolean {
    return Variables.dora > 0
}

fun riich(agari: Pair<Int, Int>, opened: List<List<Int>>, hidden: List<List<Int>>,
         kanOpened: List<Int>, kanHidden: List<Int>, isTsumo: Boolean,
         tileNumList: List<List<Int>> ): Boolean {
    return Variables.riich == Variables.Riich.RIICH && opened.size + kanOpened.size == 0
}

fun double(agari: Pair<Int, Int>, opened: List<List<Int>>, hidden: List<List<Int>>,
          kanOpened: List<Int>, kanHidden: List<Int>, isTsumo: Boolean,
          tileNumList: List<List<Int>> ): Boolean {
    return Variables.riich == Variables.Riich.DOUBLE && opened.size + kanOpened.size == 0
}

fun ippatsu(agari: Pair<Int, Int>, opened: List<List<Int>>, hidden: List<List<Int>>,
             kanOpened: List<Int>, kanHidden: List<Int>, isTsumo: Boolean,
             tileNumList: List<List<Int>> ): Boolean {
    return Variables.riich > Variables.Riich.NO && Variables.ippatsu
}
//fun sanshoku(agari: Pair<Int, Int>, opened: List<List<Int>>, hidden: List<List<Int>>,
//             kanOpened: List<Int>, kanHidden: List<Int>, isTsumo: Boolean,
//             tileNumList: List<List<Int>> ): Boolean {
//}
