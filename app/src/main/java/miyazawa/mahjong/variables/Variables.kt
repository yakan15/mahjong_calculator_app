package miyazawa.mahjong.variables

/*
 * グローバルで持っておく変数
 */
object Variables {
    interface tileId {
        val tileId: Int
        val tileName: String
    }

    enum class FieldType : tileId {
        EAST {
            override val tileName = "東"
            override val tileId = 30
        },
        SOUTH {
            override val tileName = "南"
            override val tileId = 31
        },
        WEST {
            override val tileName = "西"
            override val tileId = 32
        },
        NORTH {
            override val tileName = "北"
            override val tileId = 33
        }
    }

    enum class Riich {
        NO, RIICH, DOUBLE
    }

    enum class Bonus {
        NO, RINSHAN, HAITEI, CHANKAN
    }

    // 場風
    var field: FieldType = FieldType.EAST
    // 自風
    var me: FieldType = FieldType.SOUTH

    var dora: Int = 0
    // 0:無, 1:有, 2:ダブリー
    var ippatsu: Boolean = false

    var riich: Riich = Riich.NO
    // 嶺上開花、槍槓、河底
    var bonus: Bonus = Bonus.NO
//    var bonus: Boolean = false
    // 自摸か否か
    var isTsumo: Boolean = false
    // ダブル役満を認めるか否か
    var enableDouble = false

    fun initialize() {
        field = FieldType.EAST
        me = FieldType.SOUTH
        dora = 0
        riich = Riich.NO
        bonus = Bonus.NO
        isTsumo = false
        ippatsu = false
        enableDouble = false
    }

}
