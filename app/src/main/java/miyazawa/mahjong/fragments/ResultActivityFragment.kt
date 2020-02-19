package miyazawa.mahjong.fragments

import androidx.fragment.app.Fragment
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import miyazawa.mahjong.R
import miyazawa.mahjong.activities.ResultActivity
import miyazawa.mahjong.utils.logd
import kotlinx.android.synthetic.main.fragment_result.*
import kotlinx.android.synthetic.main.fragment_result.view.*
import miyazawa.mahjong.variables.Variables

/**
 * A placeholder fragment containing a simple view.
 */
class ResultActivityFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_result, container, false)
    }

    override fun onStart() {
        super.onStart()
        numberPickerDora.value = Variables.dora
        numberPickerDora.minValue = 0
        numberPickerDora.maxValue = 13
//        numberPickerDora.wrapSelectorWheel = true
        numberPickerDora.setOnValueChangedListener { picker, oldVal, newVal ->
            Variables.dora = newVal
        }
        switchTsumo.setOnCheckedChangeListener { buttonView, isChecked ->
            Variables.isTsumo = isChecked
        }
        switchDoubleYakuman.setOnCheckedChangeListener{ buttonView, isChecked ->
            Variables.enableDouble = isChecked
        }
        switchIppatsu.setOnCheckedChangeListener{ buttonView, isChecked ->
            Variables.ippatsu = isChecked
        }
        radioBonus.setOnCheckedChangeListener { group, checkedId ->
            Variables.bonus = when(checkedId) {
                R.id.radioBonusChankan -> Variables.Bonus.CHANKAN
                R.id.radioBonusHaitei -> Variables.Bonus.HAITEI
                R.id.radioBonusRinshan -> Variables.Bonus.RINSHAN
                else -> Variables.Bonus.NO
            }
            logd("${Variables.bonus.name}")
        }
        radioRiich.setOnCheckedChangeListener { group, checkedId ->
            Variables.riich = when(checkedId) {
                R.id.radioRiichSingle -> Variables.Riich.RIICH
                R.id.radioRiichDouble -> Variables.Riich.DOUBLE
                else -> Variables.Riich.NO
            }
            if (checkedId == R.id.radioRiichNo) {
                switchIppatsu.isChecked = false
            }
        }
        radioField.setOnCheckedChangeListener { group, checkedId ->
            Variables.field = when(checkedId) {
                R.id.radioFieldSouth -> Variables.FieldType.SOUTH
                else -> Variables.FieldType.EAST
            }
        }
        radioMe.setOnCheckedChangeListener { group, checkedId ->
            Variables.me = when(checkedId) {
                R.id.radioMeSouth -> Variables.FieldType.SOUTH
                R.id.radioMeWest -> Variables.FieldType.WEST
                R.id.radioMeNorth -> Variables.FieldType.NORTH
                else -> Variables.FieldType.EAST
            }
        }
    }

    override fun onResume() {
        super.onResume()
        if (!isVisible) {
            (activity as ResultActivity?).apply {
                this?.onConfigInvisible()
            }
        }
    }


}
