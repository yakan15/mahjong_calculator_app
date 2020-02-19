package miyazawa.mahjong

import miyazawa.mahjong.model.Constants.TILE_NUMBER
import miyazawa.mahjong.model.Constants.TILE_STRING
import miyazawa.mahjong.utils.isShuntsu
import miyazawa.mahjong.utils.isValueHead
import miyazawa.mahjong.variables.Variables
import miyazawa.mahjong.variables.Variables.field
import org.junit.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class UtilsTest {
    @Test
    fun utilsFuncTest001 () {
        assertTrue(isShuntsu(listOf(6,7,8)))
        assertFalse(isValueHead(1))
        assertFalse(isValueHead(33))
        field = Variables.FieldType.NORTH
        assertTrue(isValueHead(33))
    }

    @Test
    fun tileNumberTest () {
        assertEquals("m3", TILE_STRING[TILE_NUMBER["m3"]])
    }
}