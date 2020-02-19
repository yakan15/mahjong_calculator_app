package miyazawa.mahjong

import miyazawa.mahjong.handValidators.yonnankou
import miyazawa.mahjong.model.NormalHand
import org.junit.Test
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class HandValidatorsTest {
    @Test
    fun yonnankouuTest () {
        assertFalse(yonnankou(
            Pair(2,0), listOf(), mutableListOf(mutableListOf(1,1,1),
            mutableListOf(2,2,2), mutableListOf(3,3,3), mutableListOf(4,4), mutableListOf(5,5,5)),
            listOf(), listOf(), false, listOf()))
    }
}