package miyazawa.mahjong.variables.scores

import miyazawa.mahjong.variables.Variables.FieldType
import miyazawa.mahjong.variables.Variables.enableDouble
import miyazawa.mahjong.variables.Variables.field
import miyazawa.mahjong.variables.Variables.me

val childRonSubScores: List<List<Int>> = listOf(
    listOf(),
    listOf(0, 0, 1000, 1300, 1600, 2000, 2300, 2600, 2900, 3200, 3600),
    listOf(0, 1600, 2000, 2600, 3200, 3900, 4500, 5200, 5800, 6400, 7100),
    listOf(0, 3200, 3900, 5200, 6400, 7700, 8000, 8000, 8000, 8000, 8000),
    listOf(0, 6400, 7700, 8000, 8000, 8000, 8000, 8000, 8000, 8000, 8000)
)
val parentRonSubScores: List<List<Int>> = listOf(
    listOf(),
    listOf(0, 0, 1500, 2000, 2400, 2900, 3400, 3900, 4400, 4800, 5300),
    listOf(0, 2400, 2900, 3900, 4800, 5800, 6800, 7700, 8700, 9600, 10600),
    listOf(0, 4800, 5800, 7700, 9600, 11600, 12000, 12000, 12000, 12000, 12000),
    listOf(0, 9600, 11600, 12000, 12000, 12000, 12000, 12000, 12000, 12000, 12000)
)

val parentTsumoSubScores: List<List<Int>> = listOf(
    listOf(),
    listOf(0, 0, 500, 700, 800, 1000, 1200, 1300, 1500, 1600, 1800),
    listOf(700, 0, 1000, 1300, 1600, 2000, 2300, 2600, 2900, 3200, 3600),
    listOf(1300, 1600, 2000, 2600, 3200, 3900, 4000, 4000, 4000, 4000, 4000),
    listOf(2600, 3200, 3900, 4000, 4000, 4000, 4000, 4000, 4000, 4000, 4000)
)

val childTsumoSubScores: List<List<Int>> = listOf(
    listOf(),
    listOf(0, 0, 300, 400, 400, 500, 600, 700, 800, 800, 900),
    listOf(400, 0, 500, 700, 800, 1000, 1200, 1300, 1500, 1600, 1800),
    listOf(700, 800, 1000, 1300, 1600, 2000, 2000, 2000, 2000, 2000, 2000),
    listOf(1300, 1600, 2000, 2000, 2000, 2000, 2000, 2000, 2000, 2000, 2000, 2000)
)

fun calcChildTsumoScore(score: Int, subScore: Int): Int =
    when(score) {
        in -10..-1 -> yakumanTsumoScore(score).first
        0 -> 0
        in 1..4 -> {
            childTsumoWithScore(score, subScore)
        }
        5 -> 2_000
        6, 7 -> 3_000
        in 8..10 -> 4_000
        11, 12 -> 6_000
        else -> 8_000
    }

private fun childTsumoWithScore(score: Int, subScore: Int): Int =
    when(subScore) {
        20 -> childTsumoSubScores[score][0]
        25 -> childTsumoSubScores[score][1]
        in 20..110 -> childTsumoSubScores[score][(subScore-1)/10]
        else -> 0
    }

fun calcParentTsumoScore(score: Int, subScore: Int): Int =
    when(score) {
        in -10..-1 -> yakumanTsumoScore(score).second
        0 -> 0
        in 1..4 -> {
            parentTsumoWithScore(score, subScore)
        }
        5 -> 4_000
        6, 7 -> 6_000
        in 8..10 -> 8_000
        11, 12 -> 12_000
        else -> 16_000
    }

private fun parentTsumoWithScore(score: Int, subScore: Int): Int =
    when(subScore) {
        20 -> parentTsumoSubScores[score][0]
        25 -> parentTsumoSubScores[score][1]
        in 20..110 -> parentTsumoSubScores[score][(subScore-1)/10]
        else -> 0
    }

fun calcRonScore(score: Int, subScore: Int, isParent: Boolean): Int =
    when(isParent) {
        true -> parentRonScore(score, subScore)
        false -> childRonScore(score, subScore)
    }

private fun parentRonScore(score: Int, subScore: Int): Int =
    when(score) {
        in -10..-1 -> yakumanRonScore(score) * 3 / 2
        0 -> 0
        in 1..4 -> {
            parentRonWithSubScore(score, subScore)
        }
        5 -> 12_000
        6, 7 -> 18_000
        8, 9, 10 -> 24_000
        11, 12 -> 36_000
        else -> 48_000
    }

private fun parentRonWithSubScore(score: Int, subScore: Int): Int =
    when(subScore) {
        20 -> parentRonSubScores[score][0]
        25 -> parentRonSubScores[score][1]
        in 20..110 -> parentRonSubScores[score][(subScore-1)/10]
        else -> 0
    }

private fun childRonScore(score: Int, subScore: Int): Int =
    when(score) {
        in -10..-1 -> yakumanRonScore(score)
        0 -> 0
        in 1..4 -> {
            childRonWithSubScore(score, subScore)
        }
        5 -> 8_000
        6, 7 -> 12_000
        8, 9, 10 -> 16_000
        11, 12 -> 24_000
        else -> 32_000
    }

private fun childRonWithSubScore(score: Int, subScore: Int): Int =
    when(subScore) {
        20 -> childRonSubScores[score][0]
        25 -> childRonSubScores[score][1]
        in 22..110 -> childRonSubScores[score][(subScore-1)/10]
        else -> 0
    }

fun calcRonScore(score: Int, subScore: Int) : Int =
    when(me) {
        FieldType.EAST -> parentRonScore(score, subScore)
        else -> childRonScore(score, subScore)
    }

fun calcTsumoScore(score: Int, subScore: Int) : Pair<Int, Int> =
    Pair(calcChildTsumoScore(score, subScore),
        calcParentTsumoScore(score, subScore))

fun lessThanZero(x: Int) : Boolean = x < 0

fun yakumanRonScore(x: Int) : Int =
    when(enableDouble) {
        false -> 32_000
        else -> {
            when(x) {
                -1 -> 32_000
                else -> 64_000
            }
        }
    }

fun yakumanTsumoScore(x: Int) : Pair<Int, Int> =
    when(enableDouble) {
        false -> Pair(8000, 16000)
        else -> {
            when(x) {
                -1 -> Pair(8000, 16000)
                else -> Pair(16000, 32000)
            }
        }
    }
