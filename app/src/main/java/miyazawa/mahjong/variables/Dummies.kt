package miyazawa.mahjong.variables

import miyazawa.mahjong.model.Hand
import miyazawa.mahjong.model.Kan
import miyazawa.mahjong.model.ResultHand

object Dummies {
    val kan = Kan(mutableListOf("E"), mutableListOf())
    val hidden = mutableListOf("p1", "p1", "s6", "s7")
    val opened = mutableListOf(mutableListOf("m7", "m6", "m8"), mutableListOf("m1", "m1", "m1"))
    val hand1 = Hand(ResultHand(
        opened,
        hidden, "s8",
        kan
    ))
    val hand2 = Hand(ResultHand(mutableListOf(), mutableListOf("P"), "P", Kan(mutableListOf("W", "F"), mutableListOf("E", "N"))))
    val hand3 = Hand(ResultHand(mutableListOf(), mutableListOf("m1", "m2", "m1", "m2", "m3", "m4", "m5", "m6", "m7", "m8", "m9","m8", "m8"), "m3", null))
    val hand4 = Hand(ResultHand(mutableListOf(), mutableListOf("m7", "m8","m9", "m7", "m8", "m9", "s7", "s8", "s1", "s1", "p7", "p8","p9"), "s9", null))
    val hand5 = Hand(ResultHand(mutableListOf(mutableListOf("m7", "m8","m9"), mutableListOf("m7", "m8", "m9")), mutableListOf( "s7", "s8", "P", "P", "p7", "p8","p9"), "s9", null))
    val hand6 = Hand(ResultHand(mutableListOf(mutableListOf("m7", "m8","m9"), mutableListOf("W", "W", "W")), mutableListOf( "s7", "s8", "P", "P", "E", "E", "E"), "s9", null))
    val hand7 = Hand(ResultHand(mutableListOf(), mutableListOf("m7", "m7", "m7", "F", "F", "s6", "s7", "s8", "p4", "p4"), "F", Kan(
        mutableListOf(), mutableListOf("m2"))))
    val hand8 = Hand(ResultHand(mutableListOf(), mutableListOf("m9", "m9", "m2", "m3", "m3", "m4", "m5", "W", "W", "W", "m6", "m7", "m5"), "m4", null))
}