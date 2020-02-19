package miyazawa.mahjong.activities

import android.content.Context
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintSet
import androidx.fragment.app.FragmentManager
import kotlinx.android.synthetic.main.activity_result.*

import miyazawa.mahjong.R
import miyazawa.mahjong.model.Hand
import miyazawa.mahjong.HandImageListView
import miyazawa.mahjong.fragments.ResultActivityFragment
import miyazawa.mahjong.fragments.ResultDetailFragment
import miyazawa.mahjong.model.HandType
import miyazawa.mahjong.utils.getFieldId
import miyazawa.mahjong.utils.logd
import miyazawa.mahjong.variables.Variables
import java.lang.Exception
import java.lang.reflect.Field

class ResultActivity : AppCompatActivity() {
    lateinit var hand: Hand


    companion object {
        val HAND = "hand"
    }

    fun onConfigInvisible() {
        setHandText()
        setScoreText()
        setFieldText()
        setMeText()
        (resultDetailFragment as ResultDetailFragment).onStart()
    }

    private fun setFieldText() {
        fieldText.text = "場風：${Variables.field.tileName}"
    }

    private  fun setMeText () {
        val par = when(Variables.me) {
            Variables.FieldType.EAST -> "親"
            else -> "子"
        }
        meText.text = "自風：${Variables.me.tileName}（${par}）"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Variables.initialize()
        val _hand = intent.getSerializableExtra(HAND) as Hand?
        if (_hand == null) {
            Toast.makeText(applicationContext, "手牌情報がありません", Toast.LENGTH_LONG).show()
            finish()
        }
        hand = _hand!!
        setContentView(R.layout.activity_result)
        supportFragmentManager.beginTransaction()
            .hide(resultConfigureFragment)
            .hide(resultDetailFragment)
            .commit()
        setFields()
        detailButton.setOnClickListener {
            when(resultDetailFragment.isVisible()) {
                true -> {
                    supportFragmentManager.beginTransaction()
                        .hide(resultDetailFragment)
                        .commit()
                }
                false -> {
                    supportFragmentManager.beginTransaction()
                        .show(resultDetailFragment)
                        .commit()
                }
            }
        }
        configButton.setOnClickListener {
            when(resultConfigureFragment.isVisible()) {
                true -> {
                    supportFragmentManager.beginTransaction()
                        .hide(resultConfigureFragment)
                        .commit()
                    onConfigInvisible()
                }
                false -> {
                    supportFragmentManager.beginTransaction()
                        .show(resultConfigureFragment)
                        .commit()
                }
            }
        }
    }

    private fun setFields() {
        setFieldText()
        setMeText()
        setHandImages()
        setHandText()
        setScoreText()
    }


    private fun setHandText() {
        var text = ""
        val handNames = when (Variables.isTsumo) {
            true -> hand.tsumoHandName()
            false -> hand.ronHandName()
        }
        handNames.forEach {
            if (text != "") {
                text += ", " + it
            } else {
                text = it
            }
            if (it == HandType.DORA.handName) {
                text += Variables.dora.toString()
            }
        }
        handText.text = "${text}"
    }

    private fun setScoreText () {
        when (Variables.isTsumo) {
            true -> {
                val score = hand.tsumoScoreDetail()
                if (score.first < 0) {
                    scoreText.text = when(Variables.me) {
                        Variables.FieldType.EAST -> "役満 ${hand.tsumoScore().second} 点オール"
                        else -> "役満 ${hand.tsumoScore().first} 点、${hand.tsumoScore().second} 点"
                    }
                    return
                }
                val subScoreText = when(score.second) {
                    0 -> ""
                    else -> "${((score.second-1)/10+1)*10} 符 "
                }
                when (Variables.me) {
                    Variables.FieldType.EAST -> {
                        scoreText.text = subScoreText + "${score.first} 翻  ${hand.tsumoScore().second} 点オール"
                        logd("調整前符：${score.second}")
                    }
                    else -> {
                        scoreText.text = subScoreText + "${score.first} 翻  ${hand.tsumoScore().first} 点、${hand.tsumoScore().second} 点"
                    }
                }

            }
            false -> {
                val score = hand.ronScoreDetail()
                if (score.first < 0) {
                    scoreText.text = "役満 ${hand.ronScore()} 点"
                    return
                }

                val subScoreText = when(score.second) {
                    0 -> ""
                    else -> "${((score.second-1)/10+1)*10} 符 "
                }
                scoreText.text = subScoreText + "${score.first} 翻  ${hand.ronScore()} 点"
            }
        }

    }


    private fun setHandImages() {
        var others: MutableList<MutableList<Int>> = mutableListOf()

        hand.kanHidden.forEach {
            others.add(mutableListOf(it, 34, 34, it))
        }
        hand.opened.forEach {
            others.add(it)
        }
        hand.kanOpened.forEach {
            others.add(mutableListOf(it, it, it, it))
        }
        val constraints: ConstraintSet = ConstraintSet()
        constraints.clone(handLayout)
        constraints.setHorizontalWeight(R.id.imageMenzen, hand.hiddenWithoutAgari.size.toFloat())
        imageAgari.setHandImages(applicationContext, listOf(hand.agari))
        imageMenzen.setHandImages(applicationContext, hand.hiddenWithoutAgari)
        others.forEachIndexed() {idx, lst ->
            val str = "imageNaki" + (idx + 1).toString()
            try {
                getFieldId(str)?.let {
                    constraints.setHorizontalWeight(it, lst.size.toFloat())
                    val field = findViewById(it) as HandImageListView
                    field.setHandImages(applicationContext, lst)
                }
            } catch (e: Exception) {

            }
        }
        constraints.applyTo(handLayout)
    }

}
