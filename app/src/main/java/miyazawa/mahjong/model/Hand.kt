package miyazawa.mahjong.model

import miyazawa.mahjong.utils.copy
import miyazawa.mahjong.utils.isSameNumbers
import miyazawa.mahjong.utils.isValues
import miyazawa.mahjong.utils.logd
import java.io.Serializable

class Hand(
    val result: ResultHand
) : Serializable {
    val opened: MutableList<MutableList<Int>>
    val agari: Int
    val hidden: MutableList<Int>
    val hiddenWithoutAgari: MutableList<Int>
    val kanOpened: MutableList<Int>
    val kanHidden: MutableList<Int>
    private var tileCount: MutableMap<Int, Int> = mutableMapOf()
    val handCandidates: MutableList<HandCandidate> = mutableListOf()
    var maxRonHand: HandCandidate? = null
    var maxTsumoHand: HandCandidate? = null
    init {
        require(result.agari != null)
        agari = result.agari.toNum()
        hidden = charToNum(result.hidden)
        hiddenWithoutAgari = hidden.copy()
        hidden.add(agari)
        hidden.sort()
        opened = setToNum(result.opened)
        kanOpened = result.kan?.let {charToNum(it.opened) } ?: mutableListOf()
        kanHidden = result.kan?.let {charToNum(it.hidden) } ?: mutableListOf()
        require(checkTileNums())
        hidden.forEach{require(it != -1)}
        kanOpened.forEach{require(it != -1)}
        kanHidden.forEach{require(it != -1)}
        opened.forEach{it.forEach{require(it != -1)}}
    }

    private fun setToNum(lst: MutableList<MutableList<String>>): MutableList<MutableList<Int>> {
        val intLst: MutableList<MutableList<Int>> = mutableListOf()
        lst.forEach{triple ->
            val tmp = charToNum(triple)
            intLst.add(tmp)
        }
        return intLst
    }

    private fun charToNum(lst: MutableList<String>): MutableList<Int> {
        /*
         * apiを通して得られた情報をintに変換する。
         */
        val tmp: MutableList<Int> = mutableListOf()
        lst.forEach{str ->
            tmp.add(str.toNum())
        }
        tmp.sort()
        return tmp
    }

    private fun checkTileNums(): Boolean {
        /*
         * 各牌の枚数が異常でないかチェックする。
         * @return Boolean 正常ならtrue。
         */
        fun plusTileCount(key: Int, value: Int) {
            if (!tileCount.containsKey(key)) {
                tileCount[key] = value
            }
            tileCount[key]?.plus(value)
        }

        val count = (opened.size + kanOpened.size + kanHidden.size) * 3 + hidden.size
        println("opened: ${opened.size}, kanOpened: ${kanOpened.size}")
        println("kanHidden: ${kanHidden.size}, hidden: ${hidden}")
        if (count != 14) {
            logd("牌の合計数が一致しません")
            println("牌の合計数が一致しません")
            return false
        }
        opened.forEach{triple ->
            if (triple.size != 3) {
                println("鳴き牌が3つ組ではありません")
                return false
            }
            triple.forEach{tile ->
                plusTileCount(tile, 1)
            }
        }
        hidden.forEach{tile ->
            plusTileCount(tile, 1)
        }
        kanHidden.run {
            this.forEach { tile ->
                plusTileCount(tile, 4)
            }
        }
        kanOpened.run {
            this.forEach{tile ->
                plusTileCount(tile, 4)
            }
        }
        tileCount.forEach{(_, v) ->
            if (v > 4) {
                return false
            }
        }
        return true
    }

    fun ronHand(): HandCandidate? {
        if (maxRonHand == null) {
            ronScore()
        }
        return maxRonHand
    }

    fun tsumoHand(): HandCandidate? {
        if (maxTsumoHand == null) {
            tsumoScore()
        }
        return maxTsumoHand
    }

    fun ronScore(): Int {
        var score = -1
        handCandidates.forEach {
            val tmp = it.ronScore()
            if (tmp > score) {
                maxRonHand = it
                score = tmp
            }
        }
        return maxRonHand?.ronScore() ?: 0
    }

    fun tsumoScore(): Pair<Int, Int> {
        var score = -1
        handCandidates.forEach {
            val tmp = it.tsumoScore()
            if (tmp.first * 2 + tmp.second > score) {
                maxTsumoHand = it
                score = tmp.first * 2 + tmp.second
            }
        }
        return maxTsumoHand?.tsumoScore() ?: Pair(0,0)
    }

    fun ronHandName() : List<String> = maxRonHand?.handNameRon()?.fold(initial = mutableListOf<String>()) {
        acc, pair ->  acc.apply { this.add(pair.first) }
    } ?: listOf()

    fun tsumoHandName() : List<String> = maxTsumoHand?.handNameTsumo()?.fold(initial = mutableListOf<String>()) {
        acc, pair -> acc.apply { this.add(pair.first) }
    } ?: listOf()

    fun ronHandNameAndValue() : List<Pair<String, Int>> = maxRonHand?.handNameRon() ?: listOf()

    fun tsumoHandNameAndValue() : List<Pair<String, Int>> = maxTsumoHand?.handNameTsumo() ?: listOf()
    /*
     * 翻・符
     */
    fun ronScoreDetail() : Pair<Int, Int> = maxRonHand?.let {
        Pair(it.handCountRon(), it.subScoreRon)
    } ?: Pair(0,0)

    fun tsumoScoreDetail() : Pair<Int, Int> = maxTsumoHand?.let {
        Pair(it.handCountTsumo(), it.subScoreTsumo)
    } ?: Pair(0,0)
    /*
     * stringに対応する牌番号を返す。
     * @return return stringに対応するint。存在しない場合は-1。
     */
    private fun String.toNum(): Int = Constants.TILE_NUMBER[this] ?: -1

    fun validate(): Boolean {
        if (SevenPairs.validate(agari, hidden)) {
            handCandidates.add(SevenPairs(agari, hidden))
        }
        if (Kokushi.validate(agari, hidden)) {
            handCandidates.add(Kokushi(agari, hidden))
        }
        // 面子が正しいかどうか
        opened.forEach{triple ->
            if (triple[0] == triple[1] && triple[0] == triple[2]) {
                return@forEach
            }
            if (isValues(triple[0])) {
                println("役牌なのにコウツではない")
                return false
            }
            if (!(triple[0] == triple[1] -1 && triple[1] == triple[2] - 1 &&
                        isSameNumbers(triple[0], triple[2]))) {
                println("面子になっていない")
                return false
            }
        }
        val counts = Array(4, {Array(9, {it->0})})
        val hand = hidden.copy()
//        hand.add(agari)
//        hand.sort()
        hand.forEach{tile->
            counts[tile/9][tile%9] += 1
        }
        var nHead = -1
        counts.forEachIndexed{i, group ->
            val groupCount = group.sum()
            print(groupCount)
            when(groupCount % 3) {
                1 -> {
                    logd("牌種別の枚数が正しくありません")
                    println("牌種別の枚数が正しくありません")
                    return false
                }
                2 -> {
                    if (nHead != -1) {
                        logd("対子候補の牌種が複数存在します")
                        println("対子候補の牌種が複数存在します")
                        return false
                    }
                    nHead = i
                }
            }
        }
        if (nHead == -1) {
            logd("対子候補が存在しません")
            println("対子候補が存在しません")
            return false
        }
        val handList: MutableList<MutableList<String>> = mutableListOf()
        counts.forEachIndexed{i, group ->
            val tmp = splitToPairs(i, group.toMutableList(), nHead)
            handList.add(tmp)
        }
//        TODO("もっとマシな書き方があるはず")
        handList[0].forEach{ms ->
            handList[1].forEach{ps ->
                handList[2].forEach{ss ->
                    handList[3].forEach{values ->
                        var str = arrayListOf(ms, ps, ss, values).joinToString(separator = ":")
                        println("string before fix: ${str} ")
                        str = str.replace("(:)*".toRegex(), "$1")
                        str = str.replace("^:*".toRegex(), "")
                        str = str.replace(":*$".toRegex(), "")
                        str = str.replace("null", "")
                        str = str.replace(":*$".toRegex(), "")
                        println("string after fix: $str")
                        handCandidates.add(NormalHand(agari, opened, strToList(str),
                            kanOpened, kanHidden))
                    }
                }
            }
        }
        if (handCandidates.size == 0) {
            logd("和了できていません")
            println("和了できていません")
            return false
        }
        logd("聴牌の確認ができました")
        ronScore()
        tsumoScore()
        return true
    }


    fun strToList(listStr: String): MutableList<MutableList<Int>> {
        return listStr.split(":").map{
            it.split(",").map {
                    value -> value.toInt()
            }.toMutableList()
        }.toMutableList()
    }


    private fun splitToPairs(num: Int, count: MutableList<Int>, nHead: Int): MutableList<String>  {
        require(num in 0..3)
        require(nHead in 0..3)
        require(count.size == 9)

        fun rec(num: Int, _cursor: Int, count: MutableList<Int>, head_decided: Boolean): MutableList<String> {
            var cursor = _cursor
            val pairs: MutableList<String> = mutableListOf()
            var _count: MutableList<Int>
            var pair: MutableList<String>
            while (cursor < 8 && count[cursor] == 0) {
                cursor += 1
            }
            val tileNow = cursor + num * 9
            if (cursor == 8 && count[cursor] == 0) {
                return mutableListOf("")
            }
            if (!isValues(num * 9 - 1) && cursor < 7 && count[cursor] > 0
                && count[cursor + 1] > 0 && count[cursor + 2] > 0) {
                _count = count.copy()
                for (i in 0..2) {
                    _count[cursor + i] -= 1
                }
                pair = rec(num, cursor, _count, head_decided)
                pair.forEach{p ->
                   pairs.add(p + ":${tileNow},${tileNow + 1},${tileNow + 2}")
                }
            }
            if (count[cursor] >= 2 && !head_decided) {
                _count = count.copy()
                _count[cursor] -= 2
                pair = rec(num, cursor, _count, true)
                pair.forEach{p ->
                    pairs.add(p + ":${tileNow},${tileNow}")
                }
            }
            if (count[cursor] >= 3) {
                _count = count.copy()
                _count[cursor] -= 3
                pair = rec(num, cursor, _count, head_decided)
                pair.forEach{p ->
                    pairs.add(p + ":${tileNow},${tileNow},${tileNow}")
                }
            }
            return pairs
        }

        return  rec(num, 0, count, nHead != num)













    }
}