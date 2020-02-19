package miyazawa.mahjong

import miyazawa.mahjong.model.*
import miyazawa.mahjong.model.HandType.*
import miyazawa.mahjong.variables.Variables.enableDouble
import miyazawa.mahjong.variables.Variables.initialize
import miyazawa.mahjong.variables.Variables.me
import miyazawa.mahjong.variables.Variables.FieldType
import miyazawa.mahjong.variables.Variables.field
import org.junit.Before
import org.junit.BeforeClass
import org.junit.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class NormalHandTest {
    private val opened = mutableListOf(mutableListOf("m1","m2","m3"),
        mutableListOf("m1","m2","m3"))
    private val hidden = mutableListOf("p6", "p7","p8","p9","F","F","F")
    private val agari = "p9"
    private val kanOpened = mutableListOf<String>()
    private val kanHidden = mutableListOf<String>()
    private val kan = Kan(kanOpened, kanHidden)
    private val resultHand = ResultHand(opened, hidden, agari, kan)
    private val empKan = mutableListOf<Int>()
    private val emp = mutableListOf<MutableList<Int>>()

    @Test
    fun NormalHandTest001 () {
        // 發
        initialize()
        val hand = Hand(resultHand)
        hand.validate()
        val normalHand = hand.handCandidates[0]
        assertEquals(mutableListOf<HandTypeValidator>(HandType.GREEN), normalHand.handsRon)
        assertEquals(1500, normalHand.ronScore())
        assertEquals(Pair(400, 700), normalHand.tsumoScore())
    }

    @Test
    fun NormalHandTest002 () {
        // 九蓮
        initialize()
        val normalHand = NormalHand(7, emp, mutableListOf(mutableListOf(0,0,0),
            mutableListOf(1,2,3), mutableListOf(4,5,6), mutableListOf(7,7), mutableListOf(8,8,8)),
            empKan, empKan)
        assertEquals(mutableListOf<HandTypeValidator>(HandType.PURECHUREN), normalHand.handsRon)
        assertEquals(48000, normalHand.ronScore())
        assertEquals(Pair(8000, 16000), normalHand.tsumoScore())
        enableDouble = true
        assertEquals(96000, normalHand.ronScore())
        assertEquals(Pair(16000, 32000), normalHand.tsumoScore())
    }

    @Test
    fun NormalHandTest003 () {
        // 平和　純チャン　三色　一盃口
        initialize()
        val normalHand = NormalHand(26, emp, mutableListOf(mutableListOf(6,7,8),
            mutableListOf(6,7,8), mutableListOf(15,16,17), mutableListOf(18,18), mutableListOf(24,25,26)),
            empKan, empKan)
        assertTrue(normalHand.isMenzen)
        assertEquals(mutableListOf<HandTypeValidator>(PECO1, SANSHOKU, JUNCHAN, PINHU), normalHand.handsRon)
        assertEquals(setOf<HandTypeValidator>(PECO1, SANSHOKU, JUNCHAN, PINHU, TSUMO), normalHand.handsTsumo.toSet())
        assertEquals(18000, normalHand.ronScore())
        assertEquals(Pair(4000, 8000), normalHand.tsumoScore())
    }

    @Test
    fun NormalHandTest004 () {
        // 清一色　対々和　三暗刻　タンヤオ 自摸スー 四暗刻単騎
        initialize()
        val normalHand = NormalHand(3, emp, mutableListOf(mutableListOf(1,1,1),
            mutableListOf(2,2,2), mutableListOf(3,3,3), mutableListOf(4,4), mutableListOf(5,5,5)),
            empKan, empKan)
        assertTrue(normalHand.isMenzen)
        assertEquals(setOf<HandTypeValidator>(CHINNITSU, SANNANKOU, TOITOI, TANNYAO), normalHand.handsRon.toSet())
        assertEquals(mutableListOf<HandTypeValidator>(YONNANKOU), normalHand.handsTsumo, "四暗刻確認")
        assertEquals(36000, normalHand.ronScore())
        assertEquals(Pair(8000, 16000), normalHand.tsumoScore(), "自摸時の点数")
        val normalHand2 = NormalHand(4, emp, mutableListOf(mutableListOf(1,1,1),
            mutableListOf(2,2,2), mutableListOf(3,3,3), mutableListOf(4,4), mutableListOf(5,5,5)),
            empKan, empKan)
        assertEquals(setOf<HandTypeValidator>(YONNANTANKI), normalHand2.handsRon.toSet(), "四暗刻単騎ロン")
        assertEquals(mutableListOf<HandTypeValidator>(YONNANTANKI), normalHand2.handsTsumo, "四暗刻単騎自摸")
        assertEquals(48000, normalHand2.ronScore(), "ダブル無親")
        assertEquals(Pair(8000, 16000), normalHand2.tsumoScore(), "自摸時の点数")
        enableDouble = true
        assertEquals(Pair(16000, 32000), normalHand2.tsumoScore(), "自摸時の点数")
        assertEquals(96000, normalHand2.ronScore(), "ダブル")
        me = FieldType.NORTH
        assertEquals(64000, normalHand2.ronScore(), "子ダブル")
    }

    @Test
    fun NormalHandTest005 () {
        // 三色同刻 三暗刻
        initialize()
        val normalHand = NormalHand(0, emp, mutableListOf(mutableListOf(0,0,0),
            mutableListOf(9,9,9), mutableListOf(18,18,18), mutableListOf(2,3,4), mutableListOf(33,33)),
            empKan, empKan)
        assertTrue(normalHand.isMenzen)
        assertEquals(setOf<HandTypeValidator>(DOUKOU), normalHand.handsRon.toSet(), "ロン時役リスト")
        assertEquals(setOf<HandTypeValidator>(DOUKOU, SANNANKOU, TSUMO), normalHand.handsTsumo.toSet(),
            "自摸時役リスト")
        assertEquals(5800, normalHand.ronScore(), "ロン時点数")
        me = FieldType.NORTH
        assertEquals(3900, normalHand.ronScore(), "ロン時点数")
        assertEquals(Pair(2000, 4000), normalHand.tsumoScore(), "自摸時の点数")
    }

    @Test
    fun NormalHandTest006 () {
        // 二盃口
        initialize()
        var normalHand = NormalHand(0, emp, mutableListOf(mutableListOf(0,1,2),
            mutableListOf(0,1,2), mutableListOf(13,14,15), mutableListOf(13,14,15), mutableListOf(20,20)),
            empKan, empKan)
        assertTrue(normalHand.isMenzen)
        assertEquals(setOf<HandTypeValidator>(PINHU, PECO2), normalHand.handsRon.toSet(), "ロン時役リスト")
        assertEquals(setOf<HandTypeValidator>(PINHU, PECO2, TSUMO), normalHand.handsTsumo.toSet(),
            "自摸時役リスト")
        assertEquals(11600, normalHand.ronScore(), "ロン時点数")
        me = FieldType.NORTH
        assertEquals(7700, normalHand.ronScore(), "ロン時点数")
        assertEquals(Pair(2000, 4000), normalHand.tsumoScore(), "自摸時の点数")
        normalHand = NormalHand(1, emp, mutableListOf(mutableListOf(0,1,2),
            mutableListOf(0,1,2), mutableListOf(13,14,15), mutableListOf(13,14,15), mutableListOf(20,20)),
            empKan, empKan)
        assertEquals(setOf<HandTypeValidator>(PECO2), normalHand.handsRon.toSet(), "ロン時役リスト")
        assertEquals(setOf<HandTypeValidator>(PECO2, TSUMO), normalHand.handsTsumo.toSet(),
            "自摸時役リスト")
        me = FieldType.EAST
        assertEquals(7700, normalHand.ronScore(), "ロン時点数")
        me = FieldType.NORTH
        assertEquals(5200, normalHand.ronScore(), "ロン時点数")
        assertEquals(Pair(2000, 3900), normalHand.tsumoScore(), "自摸時の点数")
    }

    @Test
    fun NormalHandTest007 () {
        // ダブ東ホンイツチャンタ白
        initialize()
        val normalHand = NormalHand(17, mutableListOf(mutableListOf(27,27,27), mutableListOf(30,30,30)),
            mutableListOf(mutableListOf(15,16,17), mutableListOf(9,10,11), mutableListOf(32,32)),
            empKan, empKan)
        assertEquals(setOf<HandTypeValidator>(EAST, WHITE, CHANTA, HONNITSU), normalHand.handsRon.toSet())
        assertEquals(setOf<HandTypeValidator>(EAST, WHITE, CHANTA, HONNITSU), normalHand.handsTsumo.toSet())
        assertEquals(18000, normalHand.ronScore())
        assertEquals(Pair(3000, 6000), normalHand.tsumoScore())
        me = FieldType.NORTH
        assertEquals(8000, normalHand.ronScore())
        assertEquals(Pair(2000, 4000), normalHand.tsumoScore())
        field = FieldType.SOUTH
        assertEquals(7700, normalHand.ronScore())
        assertEquals(Pair(2000, 3900), normalHand.tsumoScore())
    }

    @Test
    fun NormalHandTest008 () {
        // 小三元　ホンイツ　対々和 混老頭
        initialize()
        val normalHand = NormalHand(0, mutableListOf(mutableListOf(27,27,27), mutableListOf(28,28,28)),
            mutableListOf(mutableListOf(8,8,8), mutableListOf(0,0,0), mutableListOf(29,29)),
            empKan, empKan)
        assertEquals(setOf<HandTypeValidator>(SHOUSANGEN, WHITE, GREEN,
            HONNITSU, TOITOI, HONROU), normalHand.handsRon.toSet())
        assertEquals(setOf<HandTypeValidator>(SHOUSANGEN, WHITE, GREEN,
            HONNITSU, TOITOI, HONROU), normalHand.handsTsumo.toSet())
        assertEquals(24000, normalHand.ronScore())
        assertEquals(Pair(4000, 8000), normalHand.tsumoScore())
    }

    @Test
    fun NormalHandTest009 () {
        // 小三元　ホンイツ　対々和 混老頭 三暗刻　三槓子
        initialize()
        val normalHand = NormalHand(29, mutableListOf(mutableListOf(28,28,28)),
            mutableListOf(mutableListOf(29,29)),
            empKan, mutableListOf(0,27,8)
        )
        assertEquals(setOf<HandTypeValidator>(SHOUSANGEN, WHITE, GREEN,
            HONNITSU, TOITOI, HONROU, SANNANKOU, SANKANTSU), normalHand.handsRon.toSet())
        assertEquals(setOf<HandTypeValidator>(SHOUSANGEN, WHITE, GREEN,
            HONNITSU, TOITOI, HONROU, SANNANKOU, SANKANTSU), normalHand.handsTsumo.toSet())
        assertEquals(48000, normalHand.ronScore())
        me = FieldType.WEST
        assertEquals(32000, normalHand.ronScore())
        assertEquals(Pair(8000, 16000), normalHand.tsumoScore())
    }

    @Test
    fun NormalHandTest010 () {
        // 四槓子
        initialize()
        val normalHand = NormalHand(29, mutableListOf(),
            mutableListOf(mutableListOf(29,29)),
            mutableListOf(4), mutableListOf(0,27,8)
        )
        assertEquals(setOf<HandTypeValidator>(YONKANTSU), normalHand.handsRon.toSet())
        assertEquals(setOf<HandTypeValidator>(YONKANTSU), normalHand.handsRon.toSet())
        assertEquals(48000, normalHand.ronScore())
    }

    @Test
    fun NormalHandTest011 () {
        // 緑一色
        initialize()
        val normalHand = NormalHand(19, mutableListOf(mutableListOf(19, 20, 21)),
            mutableListOf(mutableListOf(23,23,23), mutableListOf(19,19)),
            mutableListOf(25), mutableListOf(28)
        )
        assertEquals(setOf<HandTypeValidator>(ALLGREEN), normalHand.handsRon.toSet())
        assertEquals(setOf<HandTypeValidator>(ALLGREEN), normalHand.handsRon.toSet())
        assertEquals(48000, normalHand.ronScore())
    }

    @Test
    fun NormalHandTest012 () {
        // 字一色
        initialize()
        val normalHand = NormalHand(27, mutableListOf(mutableListOf(33,33,33)),
            mutableListOf(mutableListOf(30,30), mutableListOf(27,27,27)),
            mutableListOf(31), mutableListOf(28)
        )
        assertEquals(setOf<HandTypeValidator>(ALLVALUES), normalHand.handsRon.toSet())
        assertEquals(setOf<HandTypeValidator>(ALLVALUES), normalHand.handsTsumo.toSet())
        assertEquals(48000, normalHand.ronScore())
    }

    @Test
    fun NormalHandTest013 () {
        // 大四喜 + 大四喜字一色
        initialize()
        var normalHand = NormalHand(4, mutableListOf(mutableListOf(33,33,33)),
            mutableListOf(mutableListOf(30,30,30), mutableListOf(4)),
            mutableListOf(31), mutableListOf(32)
        )
        assertEquals(setOf<HandTypeValidator>(SUSHIHOU), normalHand.handsRon.toSet())
        assertEquals(setOf<HandTypeValidator>(SUSHIHOU), normalHand.handsTsumo.toSet())
        assertEquals(48000, normalHand.ronScore())
        normalHand = NormalHand(27, mutableListOf(mutableListOf(33,33,33)),
            mutableListOf(mutableListOf(30,30,30), mutableListOf(27,27)),
            mutableListOf(31), mutableListOf(32)
        )
        assertEquals(setOf<HandTypeValidator>(SUSHIHOU, ALLVALUES), normalHand.handsRon.toSet())
        assertEquals(setOf<HandTypeValidator>(SUSHIHOU, ALLVALUES), normalHand.handsTsumo.toSet())
        assertEquals(48000, normalHand.ronScore())
    }

    @Test
    fun NormalHandTest014 () {
        // 小四喜字一色
        initialize()
        var normalHand = NormalHand(4, mutableListOf(mutableListOf(33,33,33)),
            mutableListOf(mutableListOf(30,30), mutableListOf(4,4,4)),
            mutableListOf(31), mutableListOf(32)
        )
        assertEquals(setOf<HandTypeValidator>(SHOUSUSHI), normalHand.handsRon.toSet())
        assertEquals(setOf<HandTypeValidator>(SHOUSUSHI), normalHand.handsTsumo.toSet())
        assertEquals(48000, normalHand.ronScore())
        normalHand = NormalHand(27, mutableListOf(mutableListOf(33,33,33)),
            mutableListOf(mutableListOf(30,30), mutableListOf(27,27,27)),
            mutableListOf(31), mutableListOf(32)
        )
        assertEquals(setOf<HandTypeValidator>(SHOUSUSHI, ALLVALUES), normalHand.handsRon.toSet())
        assertEquals(setOf<HandTypeValidator>(SHOUSUSHI, ALLVALUES), normalHand.handsTsumo.toSet())
        assertEquals(48000, normalHand.ronScore())
    }

    @Test
    fun NormalHandTest015 () {
        // 清一色　対々和　三暗刻　三槓子　咲のやつ
        initialize()
        var normalHand = NormalHand(13, mutableListOf(),
            mutableListOf(mutableListOf(13,13), mutableListOf(12,12,12)),
            mutableListOf(9), mutableListOf(10,11))
        assertEquals(setOf<HandTypeValidator>(CHINNITSU, TOITOI, SANNANKOU, SANKANTSU), normalHand.handsRon.toSet())
//        assertEquals(48000, normalHand.ronScore())
    }
//    @Test
//    fun NormalHandTest101 () {
//        // 保留
//        // 大三元四暗刻
//        initialize()
//        val normalHand = NormalHand(1, mutableListOf(),
//            mutableListOf(mutableListOf(1,1,1), mutableListOf(3,3)),
//            empKan, mutableListOf(28,27,29)
//        )
//        assertEquals(setOf<HandTypeValidator>(DAISANGEN), normalHand.handsRon.toSet())
//        assertEquals(setOf<HandTypeValidator>(YONNANKOU, DAISANGEN), normalHand.handsTsumo.toSet())
//        enableDouble = true
//        assertEquals(96000, normalHand.ronScore())
//    }
}