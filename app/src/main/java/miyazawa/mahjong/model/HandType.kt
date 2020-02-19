package miyazawa.mahjong.model

import miyazawa.mahjong.handValidators.*
import miyazawa.mahjong.variables.Variables
import miyazawa.mahjong.variables.Variables.field
import miyazawa.mahjong.variables.Variables.me
import java.io.Serializable

interface HandTypeValidator : Serializable {
    val handName: String
    val ordinal: Int
    fun score(isMenzen: Boolean) : Int
    fun validate(agari: Pair<Int, Int>, opened: List<List<Int>>, hidden: List<List<Int>>,
                 kanOpened: List<Int>, kanHidden: List<Int>, isTsumo: Boolean, tileNumList: List<List<Int>> ): HandTypeValidator?
    fun meOrNull(bool: Boolean): HandTypeValidator? {
        if (bool){return this}
        else {return null}
    }
}

private fun windsScore(tileId: Int): Int {
    var res = 0
    if (field.tileId == tileId) {res += 1}
    if (me.tileId == tileId) {res += 1}
    return res
}

enum class HandType : HandTypeValidator {
    PINHU {
        override val handName =  "平和"
        override fun score(isMenzen: Boolean): Int =
            when(isMenzen) {
                true -> 1
                false ->0
            }
        override fun validate(agari: Pair<Int, Int>, opened: List<List<Int>>, hidden: List<List<Int>>,
                              kanOpened: List<Int>, kanHidden: List<Int>, isTsumo: Boolean, tileNumList: List<List<Int>> ): HandTypeValidator? =
            meOrNull( pinhu( agari, opened, hidden, kanOpened, kanHidden, isTsumo, tileNumList ) )
    },
    TANNYAO {
        override val handName = "タンヤオ"
        override fun score(isMenzen: Boolean): Int = 1
        override fun validate(agari: Pair<Int, Int>, opened: List<List<Int>>, hidden: List<List<Int>>,
                              kanOpened: List<Int>, kanHidden: List<Int>, isTsumo: Boolean, tileNumList: List<List<Int>> ): HandTypeValidator? =
            meOrNull( tannyao( agari, opened, hidden, kanOpened, kanHidden, isTsumo, tileNumList ) )
    },
    EAST {
        private val tileNum = 30
        override val handName = "東"
        override fun score(isMenzen: Boolean): Int = windsScore(tileNum)
        override fun validate(agari: Pair<Int, Int>, opened: List<List<Int>>, hidden: List<List<Int>>,
                              kanOpened: List<Int>, kanHidden: List<Int>, isTsumo: Boolean, tileNumList: List<List<Int>> ): HandTypeValidator? =
            meOrNull( values( agari, opened, hidden, kanOpened, kanHidden, isTsumo, tileNumList, tileNum ) )
    },
    SOUTH {
        private val tileNum = 31
        override val handName = "南"
        override fun score(isMenzen: Boolean): Int = windsScore(tileNum)
        override fun validate(agari: Pair<Int, Int>, opened: List<List<Int>>, hidden: List<List<Int>>,
                              kanOpened: List<Int>, kanHidden: List<Int>, isTsumo: Boolean, tileNumList: List<List<Int>> ): HandTypeValidator? =
            meOrNull( values( agari, opened, hidden, kanOpened, kanHidden, isTsumo, tileNumList, tileNum ) )
    },
    WEST {
        private val tileNum = 32
        override val handName = "西"
        override fun score(isMenzen: Boolean): Int = windsScore(tileNum)
        override fun validate(agari: Pair<Int, Int>, opened: List<List<Int>>, hidden: List<List<Int>>,
                              kanOpened: List<Int>, kanHidden: List<Int>, isTsumo: Boolean, tileNumList: List<List<Int>> ): HandTypeValidator? =
            meOrNull( values( agari, opened, hidden, kanOpened, kanHidden, isTsumo, tileNumList, tileNum ) )
    },
    NORTH {
        private val tileNum = 33
        override val handName = "北"
        override fun score(isMenzen: Boolean): Int = windsScore(tileNum)
        override fun validate(agari: Pair<Int, Int>, opened: List<List<Int>>, hidden: List<List<Int>>,
                              kanOpened: List<Int>, kanHidden: List<Int>, isTsumo: Boolean, tileNumList: List<List<Int>> ): HandTypeValidator? =
            meOrNull( values( agari, opened, hidden, kanOpened, kanHidden, isTsumo, tileNumList, tileNum ) )
    },
    WHITE {
        private val tileNum = 27
        override val handName = "白"
        override fun score(isMenzen: Boolean): Int = 1
        override fun validate(agari: Pair<Int, Int>, opened: List<List<Int>>, hidden: List<List<Int>>,
                              kanOpened: List<Int>, kanHidden: List<Int>, isTsumo: Boolean, tileNumList: List<List<Int>> ): HandTypeValidator? =
            meOrNull( values( agari, opened, hidden, kanOpened, kanHidden, isTsumo, tileNumList, tileNum ) )
    },
    GREEN {
        private val tileNum = 28
        override val handName = "發"
        override fun score(isMenzen: Boolean): Int = 1
        override fun validate(agari: Pair<Int, Int>, opened: List<List<Int>>, hidden: List<List<Int>>,
                              kanOpened: List<Int>, kanHidden: List<Int>, isTsumo: Boolean, tileNumList: List<List<Int>> ): HandTypeValidator? =
            meOrNull( values( agari, opened, hidden, kanOpened, kanHidden, isTsumo, tileNumList, tileNum ) )
    },
    RED {
        private val tileNum = 29
        override val handName = "中"
        override fun score(isMenzen: Boolean): Int = 1
        override fun validate(agari: Pair<Int, Int>, opened: List<List<Int>>, hidden: List<List<Int>>,
                              kanOpened: List<Int>, kanHidden: List<Int>, isTsumo: Boolean, tileNumList: List<List<Int>> ): HandTypeValidator? =
            meOrNull( values( agari, opened, hidden, kanOpened, kanHidden, isTsumo, tileNumList, tileNum ) )
    },
    PECO1 {
        override val handName =  "イーペーコー"
        override fun score(isMenzen: Boolean): Int =
            when(isMenzen) {
                true -> 1
                false ->0
            }
        override fun validate(agari: Pair<Int, Int>, opened: List<List<Int>>, hidden: List<List<Int>>,
                              kanOpened: List<Int>, kanHidden: List<Int>, isTsumo: Boolean, tileNumList: List<List<Int>> ): HandTypeValidator? =
            meOrNull( peco1( agari, opened, hidden, kanOpened, kanHidden, isTsumo, tileNumList ) )
    },
    CHANTA {
        override val handName =  "チャンタ"
        override fun score(isMenzen: Boolean): Int =
            when(isMenzen) {
                true -> 2
                false -> 1
            }
        override fun validate(agari: Pair<Int, Int>, opened: List<List<Int>>, hidden: List<List<Int>>,
                              kanOpened: List<Int>, kanHidden: List<Int>, isTsumo: Boolean, tileNumList: List<List<Int>> ): HandTypeValidator? =
            meOrNull( chanta( agari, opened, hidden, kanOpened, kanHidden, isTsumo, tileNumList ) )
    },
    ITTSU {
        override val handName =  "一気通貫"
        override fun score(isMenzen: Boolean): Int =
            when(isMenzen) {
                true -> 2
                false -> 1
            }
        override fun validate(agari: Pair<Int, Int>, opened: List<List<Int>>, hidden: List<List<Int>>,
                              kanOpened: List<Int>, kanHidden: List<Int>, isTsumo: Boolean, tileNumList: List<List<Int>> ): HandTypeValidator? =
            meOrNull( ittsu( agari, opened, hidden, kanOpened, kanHidden, isTsumo, tileNumList ) )
    },
    SANSHOKU {
        override val handName =  "三色同順"
        override fun score(isMenzen: Boolean): Int =
            when(isMenzen) {
                true -> 2
                false ->1
            }
        override fun validate(agari: Pair<Int, Int>, opened: List<List<Int>>, hidden: List<List<Int>>,
                              kanOpened: List<Int>, kanHidden: List<Int>, isTsumo: Boolean, tileNumList: List<List<Int>> ): HandTypeValidator? =
            meOrNull( sanshoku( agari, opened, hidden, kanOpened, kanHidden, isTsumo, tileNumList ) )
    },
    DOUKOU {
        override val handName =  "三色同刻"
        override fun score(isMenzen: Boolean): Int = 2
        override fun validate(agari: Pair<Int, Int>, opened: List<List<Int>>, hidden: List<List<Int>>,
                              kanOpened: List<Int>, kanHidden: List<Int>, isTsumo: Boolean, tileNumList: List<List<Int>> ): HandTypeValidator? =
            meOrNull( doukou( agari, opened, hidden, kanOpened, kanHidden, isTsumo, tileNumList ) )
    },
    SANKANTSU {
        override val handName =  "三槓子"
        override fun score(isMenzen: Boolean): Int = 2
        override fun validate(agari: Pair<Int, Int>, opened: List<List<Int>>, hidden: List<List<Int>>,
                              kanOpened: List<Int>, kanHidden: List<Int>, isTsumo: Boolean, tileNumList: List<List<Int>> ): HandTypeValidator? =
            meOrNull( sankantsu( agari, opened, hidden, kanOpened, kanHidden, isTsumo, tileNumList ) )
    },
    TOITOI {
        override val handName =  "対々和"
        override fun score(isMenzen: Boolean): Int = 2
        override fun validate(agari: Pair<Int, Int>, opened: List<List<Int>>, hidden: List<List<Int>>,
                              kanOpened: List<Int>, kanHidden: List<Int>, isTsumo: Boolean, tileNumList: List<List<Int>> ): HandTypeValidator? =
            meOrNull( toitoi( agari, opened, hidden, kanOpened, kanHidden, isTsumo, tileNumList ) )
    },
    SANNANKOU {
        override val handName =  "三暗刻"
        override fun score(isMenzen: Boolean): Int = 2
        override fun validate(agari: Pair<Int, Int>, opened: List<List<Int>>, hidden: List<List<Int>>,
                              kanOpened: List<Int>, kanHidden: List<Int>, isTsumo: Boolean, tileNumList: List<List<Int>> ): HandTypeValidator? =
            meOrNull( sannankou( agari, opened, hidden, kanOpened, kanHidden, isTsumo, tileNumList ) )
    },
    SHOUSANGEN {
        override val handName =  "小三元"
        override fun score(isMenzen: Boolean): Int = 2
        override fun validate(agari: Pair<Int, Int>, opened: List<List<Int>>, hidden: List<List<Int>>,
                              kanOpened: List<Int>, kanHidden: List<Int>, isTsumo: Boolean, tileNumList: List<List<Int>> ): HandTypeValidator? =
            meOrNull( shousangen( agari, opened, hidden, kanOpened, kanHidden, isTsumo, tileNumList ) )
    },
    HONROU {
        override val handName =  "混老頭"
        override fun score(isMenzen: Boolean): Int = 2
        override fun validate(agari: Pair<Int, Int>, opened: List<List<Int>>, hidden: List<List<Int>>,
                              kanOpened: List<Int>, kanHidden: List<Int>, isTsumo: Boolean, tileNumList: List<List<Int>> ): HandTypeValidator? =
            meOrNull( honrou( agari, opened, hidden, kanOpened, kanHidden, isTsumo, tileNumList ) )
    },
    SEVENPAIRS {
        override val handName =  "七対子"
        override fun score(isMenzen: Boolean): Int = 2
        // 暫定
        override fun validate(agari: Pair<Int, Int>, opened: List<List<Int>>, hidden: List<List<Int>>,
                              kanOpened: List<Int>, kanHidden: List<Int>, isTsumo: Boolean, tileNumList: List<List<Int>> ): HandTypeValidator? = null
    },
    JUNCHAN {
        override val handName =  "純チャン"
        override fun score(isMenzen: Boolean): Int =
            when(isMenzen) {
                true -> 3
                false ->2
            }
        override fun validate(agari: Pair<Int, Int>, opened: List<List<Int>>, hidden: List<List<Int>>,
                              kanOpened: List<Int>, kanHidden: List<Int>, isTsumo: Boolean, tileNumList: List<List<Int>> ): HandTypeValidator? =
            meOrNull( junchan( agari, opened, hidden, kanOpened, kanHidden, isTsumo, tileNumList ) )
    },
    HONNITSU {
        override val handName =  "混一色"
        override fun score(isMenzen: Boolean): Int =
            when(isMenzen) {
                true -> 3
                false ->2
            }
        override fun validate(agari: Pair<Int, Int>, opened: List<List<Int>>, hidden: List<List<Int>>,
                              kanOpened: List<Int>, kanHidden: List<Int>, isTsumo: Boolean, tileNumList: List<List<Int>> ): HandTypeValidator? =
            meOrNull( honnitsu( agari, opened, hidden, kanOpened, kanHidden, isTsumo, tileNumList ) )
    },
    PECO2 {
        override val handName =  "リャンペーコー"
        override fun score(isMenzen: Boolean): Int =
            when(isMenzen) {
                true -> 3
                false -> 0
            }
        override fun validate(agari: Pair<Int, Int>, opened: List<List<Int>>, hidden: List<List<Int>>,
                              kanOpened: List<Int>, kanHidden: List<Int>, isTsumo: Boolean, tileNumList: List<List<Int>> ): HandTypeValidator? =
            meOrNull( peco2( agari, opened, hidden, kanOpened, kanHidden, isTsumo, tileNumList ) )
    },
    CHINNITSU {
        override val handName =  "清一色"
        override fun score(isMenzen: Boolean): Int =
            when(isMenzen) {
                true -> 6
                false -> 5
            }
        override fun validate(agari: Pair<Int, Int>, opened: List<List<Int>>, hidden: List<List<Int>>,
                              kanOpened: List<Int>, kanHidden: List<Int>, isTsumo: Boolean, tileNumList: List<List<Int>> ): HandTypeValidator? =
            meOrNull( chinnitsu( agari, opened, hidden, kanOpened, kanHidden, isTsumo, tileNumList ) )
    },
    DAISANGEN {
        override val handName =  "大三元"
        override fun score(isMenzen: Boolean): Int = -1
        override fun validate(agari: Pair<Int, Int>, opened: List<List<Int>>, hidden: List<List<Int>>,
                              kanOpened: List<Int>, kanHidden: List<Int>, isTsumo: Boolean, tileNumList: List<List<Int>> ): HandTypeValidator? =
            meOrNull( daisangen( agari, opened, hidden, kanOpened, kanHidden, isTsumo, tileNumList ) )
    },
    YONNANKOU {
        override val handName =  "四暗刻"
        override fun score(isMenzen: Boolean): Int =
            when(isMenzen) {
                true -> -1
                false -> 0
            }
        override fun validate(agari: Pair<Int, Int>, opened: List<List<Int>>, hidden: List<List<Int>>,
                              kanOpened: List<Int>, kanHidden: List<Int>, isTsumo: Boolean, tileNumList: List<List<Int>> ): HandTypeValidator? =
            meOrNull( yonnankou( agari, opened, hidden, kanOpened, kanHidden, isTsumo, tileNumList ) )
    },
    YONNANTANKI {
        override val handName =  "四暗刻単騎"
        override fun score(isMenzen: Boolean): Int = -2
        override fun validate(agari: Pair<Int, Int>, opened: List<List<Int>>, hidden: List<List<Int>>,
                              kanOpened: List<Int>, kanHidden: List<Int>, isTsumo: Boolean, tileNumList: List<List<Int>> ): HandTypeValidator? =
            meOrNull( yonnantannki( agari, opened, hidden, kanOpened, kanHidden, isTsumo, tileNumList ) )
    },
    ALLVALUES {
        override val handName =  "字一色"
        override fun score(isMenzen: Boolean): Int = -1
        override fun validate(agari: Pair<Int, Int>, opened: List<List<Int>>, hidden: List<List<Int>>,
                              kanOpened: List<Int>, kanHidden: List<Int>, isTsumo: Boolean, tileNumList: List<List<Int>> ): HandTypeValidator? =
            meOrNull( allvalues( agari, opened, hidden, kanOpened, kanHidden, isTsumo, tileNumList ) )
    },
    ALLGREEN {
        override val handName =  "緑一色"
        override fun score(isMenzen: Boolean): Int = -1
        override fun validate(agari: Pair<Int, Int>, opened: List<List<Int>>, hidden: List<List<Int>>,
                              kanOpened: List<Int>, kanHidden: List<Int>, isTsumo: Boolean, tileNumList: List<List<Int>> ): HandTypeValidator? =
            meOrNull( allgreen( agari, opened, hidden, kanOpened, kanHidden, isTsumo, tileNumList ) )
    },
    CHINROU {
        override val handName =  "清老頭"
        override fun score(isMenzen: Boolean): Int = -1
        override fun validate(agari: Pair<Int, Int>, opened: List<List<Int>>, hidden: List<List<Int>>,
                              kanOpened: List<Int>, kanHidden: List<Int>, isTsumo: Boolean, tileNumList: List<List<Int>> ): HandTypeValidator? =
            meOrNull( chinrou( agari, opened, hidden, kanOpened, kanHidden, isTsumo, tileNumList ) )
    },
    KOKUSHI{
        override val handName =  "国士無双"
        override fun score(isMenzen: Boolean): Int =
            when(isMenzen) {
                true -> -1
                false -> 0
            }
         // 暫定
        override fun validate(agari: Pair<Int, Int>, opened: List<List<Int>>, hidden: List<List<Int>>,
                              kanOpened: List<Int>, kanHidden: List<Int>, isTsumo: Boolean, tileNumList: List<List<Int>> ): HandTypeValidator? = null
    },
    PUREKOKUSHI{
        override val handName =  "国士無双13面"
        override fun score(isMenzen: Boolean): Int =
            when(isMenzen) {
                true -> -2
                false -> 0
            }
        // 暫定
        override fun validate(agari: Pair<Int, Int>, opened: List<List<Int>>, hidden: List<List<Int>>,
                              kanOpened: List<Int>, kanHidden: List<Int>, isTsumo: Boolean, tileNumList: List<List<Int>> ): HandTypeValidator? = null
    },
    SUSHIHOU {
        override val handName =  "四喜和"
        override fun score(isMenzen: Boolean): Int = -2
        override fun validate(agari: Pair<Int, Int>, opened: List<List<Int>>, hidden: List<List<Int>>,
                              kanOpened: List<Int>, kanHidden: List<Int>, isTsumo: Boolean, tileNumList: List<List<Int>> ): HandTypeValidator? =
            meOrNull( sushihou( agari, opened, hidden, kanOpened, kanHidden, isTsumo, tileNumList ) )
    },
    SHOUSUSHI {
        override val handName =  "小四喜"
        override fun score(isMenzen: Boolean): Int = -1
        override fun validate(agari: Pair<Int, Int>, opened: List<List<Int>>, hidden: List<List<Int>>,
                              kanOpened: List<Int>, kanHidden: List<Int>, isTsumo: Boolean, tileNumList: List<List<Int>> ): HandTypeValidator? =
            meOrNull( shousushi( agari, opened, hidden, kanOpened, kanHidden, isTsumo, tileNumList ) )
    },
    YONKANTSU {
        override val handName =  "四槓子"
        override fun score(isMenzen: Boolean): Int = -1
        override fun validate(agari: Pair<Int, Int>, opened: List<List<Int>>, hidden: List<List<Int>>,
                              kanOpened: List<Int>, kanHidden: List<Int>, isTsumo: Boolean, tileNumList: List<List<Int>> ): HandTypeValidator? =
            meOrNull( yonkantsu( agari, opened, hidden, kanOpened, kanHidden, isTsumo, tileNumList ) )
    },
    CHUREN {
        override val handName =  "九蓮宝燈"
        override fun score(isMenzen: Boolean): Int =
            when(isMenzen) {
                true -> -1
                false -> 0
            }
        override fun validate(agari: Pair<Int, Int>, opened: List<List<Int>>, hidden: List<List<Int>>,
                              kanOpened: List<Int>, kanHidden: List<Int>, isTsumo: Boolean, tileNumList: List<List<Int>> ): HandTypeValidator? =
            meOrNull( churen( agari, opened, hidden, kanOpened, kanHidden, isTsumo, tileNumList ) )
    },
    PURECHUREN {
        override val handName =  "純正九蓮宝燈"
        override fun score(isMenzen: Boolean): Int =
            when(isMenzen) {
                true -> -2
                false -> 0
            }
        override fun validate(agari: Pair<Int, Int>, opened: List<List<Int>>, hidden: List<List<Int>>,
                              kanOpened: List<Int>, kanHidden: List<Int>, isTsumo: Boolean, tileNumList: List<List<Int>> ): HandTypeValidator? =
            meOrNull( purechuren( agari, opened, hidden, kanOpened, kanHidden, isTsumo, tileNumList ) )
    },
    TSUMO {
        override val handName =  "ツモ"
        override fun score(isMenzen: Boolean): Int =
            when(isMenzen) {
                true -> 1
                false -> 0
            }
        override fun validate(agari: Pair<Int, Int>, opened: List<List<Int>>, hidden: List<List<Int>>,
                              kanOpened: List<Int>, kanHidden: List<Int>, isTsumo: Boolean, tileNumList: List<List<Int>> ): HandTypeValidator? =
            meOrNull( tsumo( agari, opened, hidden, kanOpened, kanHidden, isTsumo, tileNumList ) )
    },
    RINSHAN {
        override val handName =  "嶺上開花"
        override fun score(isMenzen: Boolean): Int = 1
        override fun validate(agari: Pair<Int, Int>, opened: List<List<Int>>, hidden: List<List<Int>>,
                              kanOpened: List<Int>, kanHidden: List<Int>, isTsumo: Boolean, tileNumList: List<List<Int>> ): HandTypeValidator? =
            meOrNull( rinshan( agari, opened, hidden, kanOpened, kanHidden, isTsumo, tileNumList ) )
    },
    CHANKAN {
        override val handName =  "槍槓"
        override fun score(isMenzen: Boolean): Int = 1
        override fun validate(agari: Pair<Int, Int>, opened: List<List<Int>>, hidden: List<List<Int>>,
                              kanOpened: List<Int>, kanHidden: List<Int>, isTsumo: Boolean, tileNumList: List<List<Int>> ): HandTypeValidator? =
            meOrNull( chankan( agari, opened, hidden, kanOpened, kanHidden, isTsumo, tileNumList ) )
    },
    HAITEI {
        override val handName =  "海底撈月"
        override fun score(isMenzen: Boolean): Int =
            when (Variables.isTsumo) {
                true -> 1
                false -> 0
            }
        override fun validate(agari: Pair<Int, Int>, opened: List<List<Int>>, hidden: List<List<Int>>,
                              kanOpened: List<Int>, kanHidden: List<Int>, isTsumo: Boolean, tileNumList: List<List<Int>> ): HandTypeValidator? =
            meOrNull( haitei( agari, opened, hidden, kanOpened, kanHidden, isTsumo, tileNumList ) )
    },
    HOUTEI {
        override val handName =  "河底撈魚"
        override fun score(isMenzen: Boolean): Int =
            when (Variables.isTsumo) {
                true -> 0
                false -> 1
            }
        override fun validate(agari: Pair<Int, Int>, opened: List<List<Int>>, hidden: List<List<Int>>,
                              kanOpened: List<Int>, kanHidden: List<Int>, isTsumo: Boolean, tileNumList: List<List<Int>> ): HandTypeValidator? =
            meOrNull( haitei( agari, opened, hidden, kanOpened, kanHidden, isTsumo, tileNumList ) )
    },
    DORA {
        override val handName =  "ドラ"
        override fun score(isMenzen: Boolean): Int = Variables.dora
        override fun validate(agari: Pair<Int, Int>, opened: List<List<Int>>, hidden: List<List<Int>>,
                              kanOpened: List<Int>, kanHidden: List<Int>, isTsumo: Boolean, tileNumList: List<List<Int>> ): HandTypeValidator? =
            meOrNull( dora( agari, opened, hidden, kanOpened, kanHidden, isTsumo, tileNumList ) )
    },
    RIICH {
        override val handName =  "立直"
        override fun score(isMenzen: Boolean): Int =
            when (isMenzen) {
                true -> 1
                false -> 0
            }
        override fun validate(agari: Pair<Int, Int>, opened: List<List<Int>>, hidden: List<List<Int>>,
                              kanOpened: List<Int>, kanHidden: List<Int>, isTsumo: Boolean, tileNumList: List<List<Int>> ): HandTypeValidator? =
            meOrNull( riich( agari, opened, hidden, kanOpened, kanHidden, isTsumo, tileNumList ) )
    },
    DOUBLE {
        override val handName =  "ダブル立直"
        override fun score(isMenzen: Boolean): Int =
            when (isMenzen) {
                true -> 2
                false -> 0
            }
        override fun validate(agari: Pair<Int, Int>, opened: List<List<Int>>, hidden: List<List<Int>>,
                              kanOpened: List<Int>, kanHidden: List<Int>, isTsumo: Boolean, tileNumList: List<List<Int>> ): HandTypeValidator? =
            meOrNull( double( agari, opened, hidden, kanOpened, kanHidden, isTsumo, tileNumList ) )
    },
    IPPATSU {
        override val handName =  "一発"
        override fun score(isMenzen: Boolean): Int =
            when (isMenzen) {
                true -> 1
                false -> 0
            }
        override fun validate(agari: Pair<Int, Int>, opened: List<List<Int>>, hidden: List<List<Int>>,
                              kanOpened: List<Int>, kanHidden: List<Int>, isTsumo: Boolean, tileNumList: List<List<Int>> ): HandTypeValidator? =
            meOrNull( ippatsu( agari, opened, hidden, kanOpened, kanHidden, isTsumo, tileNumList ) )
    },
}
