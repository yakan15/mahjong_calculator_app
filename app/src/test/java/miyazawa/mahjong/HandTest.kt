package miyazawa.mahjong

import android.util.Log
import miyazawa.mahjong.model.Hand
import miyazawa.mahjong.model.Kan
import miyazawa.mahjong.model.NormalHand
import miyazawa.mahjong.model.ResultHand
import org.junit.Test
import org.junit.runner.RunWith
import org.powermock.core.classloader.annotations.PrepareForTest
import org.powermock.modules.junit4.PowerMockRunner
import kotlin.test.assertEquals
import kotlin.test.assertFails
import kotlin.test.assertNotEquals

class HandTest {
    private val opened = mutableListOf(mutableListOf("m1","m2","m3"),
        mutableListOf("m1","m2","m3"))
    private val hidden = mutableListOf("p6", "p7","p8","p9","f","f","f")
    private val agari = "p9"
    private val kanOpened = mutableListOf<String>()
    private val kanHidden = mutableListOf<String>()
    private val kan = Kan(kanOpened, kanHidden)
    private val resultHand = ResultHand(opened, hidden, agari, kan)

    @Test
    fun HandTest001 () {
        val hand = Hand(resultHand)
        assertEquals(17, hand.agari)
        assertEquals(mutableListOf(mutableListOf(0, 1, 2),
            mutableListOf(0, 1, 2)), hand.opened)
        assertEquals(mutableListOf(14, 15, 16, 17, 17, 28, 28, 28), hand.hidden)
        assertEquals(mutableListOf(), hand.kanOpened)
        assertEquals(mutableListOf(), hand.kanHidden)
        assertEquals(true, hand.validate())
        assertEquals(1, hand.handCandidates.size)
        val hand2 = Hand(ResultHand(opened, hidden, "m4", kan))
        assertEquals(false, hand2.validate())
    }

    @Test
    fun HandTest002 () {
        assertFails { Hand(ResultHand(opened, hidden, null, kan)) }
        assertFails { Hand(ResultHand(opened, (hidden + "f").toMutableList(), null, kan)) }
        assertFails { Hand(ResultHand(opened, (hidden + "f").toMutableList(), "p9", kan)) }
        assertFails { Hand(ResultHand(opened, hidden.toMutableList(), "a", kan)) }
    }

    @Test
    fun HandTest003 () {
        val hidden = mutableListOf("m2","m2","m2","m3","m3","m3","m4","m4","m4","m6","m6","s1","s2")
        val hand = Hand(ResultHand(mutableListOf<MutableList<String>>(),hidden, "s3", kan ))
        hand.validate()
        hand.handCandidates.forEach {
        }
        assertEquals(2, hand.handCandidates.size)
        assertEquals(4800, hand.ronScore())
        assertEquals(Pair(1300, 2600), hand.tsumoScore())
        val tsumoHand = hand.tsumoHand()
        val ronHand = hand.ronHand()
        assertEquals(3, tsumoHand?.handCountTsumo())
        assertEquals(36, tsumoHand?.subScoreTsumo)
        assertEquals(2, tsumoHand?.handCountRon())
        assertEquals(44, tsumoHand?.subScoreRon)
    }

    @Test
    fun HandTest004 () {
        val hidden = mutableListOf("m1","m1","m1","m2","m3","m4","m5","m6","m7","m8","m9","m9","m9")
        val hand = Hand(ResultHand(mutableListOf<MutableList<String>>(),hidden, "m9", kan ))
        hand.validate()
        hand.handCandidates.forEach {
        }
        assertEquals(2, hand.handCandidates.size)
        assertEquals(48000, hand.ronScore())
        assertEquals(Pair(8000, 16000), hand.tsumoScore())
    }

    @Test
    fun HandTest005 () {
        val hidden = mutableListOf("p1", "p1", "s6", "s7", "S", "S", "S")
        val opened = mutableListOf(mutableListOf("m7", "m6", "m8"), mutableListOf("m1", "m1", "m1"))
        val hand = Hand(ResultHand(opened, hidden, "s8", kan))
        hand.validate()

    }

    @Test
    fun HandTest006 () {
        val kan = Kan(mutableListOf("W"), mutableListOf())
        val hidden = mutableListOf("p1", "p1", "s6", "s7")
        val opened = mutableListOf(mutableListOf("m7", "m6", "m8"), mutableListOf("m1", "m1", "m1"))
        val hand = Hand(ResultHand(opened, hidden, "s8", kan))
        hand.validate()

    }

    @Test
    fun HandTest007 () {
        val hand2 = Hand(ResultHand(mutableListOf(), mutableListOf("P"), "P", Kan(mutableListOf("W", "F"), mutableListOf("E", "N"))))
        hand2.validate()
    }
}