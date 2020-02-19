package miyazawa.mahjong.model

object Constants {
    /*
     * String to Int
     */
    @JvmField val TILE_NUMBER: Map<String, Int> = mapOf(
        "m1" to 0, "m2" to 1, "m3" to 2, "m4" to 3, "m5" to 4, "m6" to 5, "m7" to 6, "m8" to 7, "m9" to 8,
        "p1" to 9, "p2" to 10, "p3" to 11, "p4" to 12, "p5" to 13, "p6" to 14, "p7" to 15, "p8" to 16, "p9" to 17,
        "s1" to 18, "s2" to 19, "s3" to 20, "s4" to 21, "s5" to 22, "s6" to 23, "s7" to 24, "s8" to 25, "s9" to 26,
        "P" to 27, "F" to 28, "C" to 29, "E" to 30, "S" to 31, "W" to 32, "N" to 33
    )

    /*
     * Int to String
     * ほとんどimage逆引き用
     */
    @JvmField val TILE_STRING: Map<Int, String> = mutableMapOf<Int, String>().also {
        TILE_NUMBER.forEach { (k, v) -> it.put(v, k) }
    }.also {it.put(34, "back")}.toMap()

    @JvmField val YAOCHU_NUMBERS: List<Int> = listOf(
        0, 8, 9 ,17, 18, 26, 27, 28, 29, 30, 31, 32, 33
    )

    const val NUM_TILES = 34

    @JvmField val HAND_ID: Map<Int, String> = mapOf(
        0 to "平和", 1 to "立直", 2 to "タンヤオ",
        3 to "東", 4 to "南", 5 to "西", 6 to "北",
        7 to "白", 8 to "發", 9 to "中",
        10 to "イーペーコー",
        11 to "チャンタ", 12 to "一気通貫", 13 to "三色同順", 14 to "ダブル立直",
        15 to "三色同刻", 16 to "三槓子", 17 to "対々和", 18 to "三暗刻",
        19 to "小三元", 20 to "混老頭", 21 to "七対子",
        22 to "純チャン", 23 to "混一色", 24 to "リャンペーコー",
        25 to "清一色",
        26 to "天和", 27 to "地和", 28 to "大三元", 28 to "四暗刻", 30 to "字一色",
        31 to "緑一色", 32 to "清老頭", 33 to "国士無双", 34 to "大四喜",
        35 to "小四喜", 36 to "四槓子", 37 to "九蓮宝燈",
        38 to "四暗刻単騎", 39 to "国士無双13面", 40 to "純正九蓮宝燈"
    )
}

