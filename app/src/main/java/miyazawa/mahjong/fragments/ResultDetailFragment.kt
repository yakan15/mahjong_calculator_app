package miyazawa.mahjong.fragments

import android.content.Context
import android.graphics.Color
import android.net.Uri
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintSet
import kotlinx.android.synthetic.main.activity_result.*
import kotlinx.android.synthetic.main.fragment_result_detail.*
import miyazawa.mahjong.HandDetailAdapter
import miyazawa.mahjong.HandImageWithTextListView
import miyazawa.mahjong.R
import miyazawa.mahjong.SubScoreDetailAdapter
import miyazawa.mahjong.activities.ResultActivity
import miyazawa.mahjong.handValidators.agariRelatedYakumans
import miyazawa.mahjong.handValidators.kokushiHands
import miyazawa.mahjong.handValidators.yakuman
import miyazawa.mahjong.handValidators.yakumanHands
import miyazawa.mahjong.model.*
import miyazawa.mahjong.utils.getFieldId
import miyazawa.mahjong.variables.Variables
import kotlin.reflect.typeOf

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Activities that contain this fragment must implement the
 * [ResultDetailFragment.OnFragmentInteractionListener] interface
 * to handle interaction events.
 * Use the [ResultDetailFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class ResultDetailFragment : Fragment() {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null
//    private var listener: OnFragmentInteractionListener? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_result_detail, container, false)
    }

    override fun onStart() {
        super.onStart()
        setScene()
    }

    fun setScene() {
        setHandDetailText()
        setHandValueDetailText()
        setSubScoreInfo()

    }

    private fun resetHandImages() {
        val constraints = ConstraintSet()
        constraints.clone(handDetailLayout)
        for (i in 1..5) {
            getFieldId("imageDetailHand${i}")?.let {
                constraints.setHorizontalWeight(it,0.toFloat())
                (view?.findViewById<View?>(it) as HandImageWithTextListView).apply {
                    removeAllViews()
                }
            }
        }
        constraints.applyTo(handDetailLayout)
    }

    private fun setSubScoreInfo() {
        resetHandImages()
        (activity as ResultActivity).let {act ->
            val hand = when (Variables.isTsumo) {
                true -> act.hand.maxTsumoHand
                false -> act.hand.maxRonHand
            }
//            TODO チートイと国士の処理
//            if (hand is SevenPairs) {
//                forSevenPairs(hand)
//            } else if (hand is Kokushi) {
//                forYakumanHands(hand)
//            }
            val constraints = ConstraintSet()
            constraints.clone(handDetailLayout)
            val subScoreHelper = SubScoreDisplayHelper(hand as NormalHand, Variables.isTsumo)
            val handList = subScoreHelper.handList
            handList.forEachIndexed { index, triple ->
                getFieldId("imageDetailHand${index+1}")?.let {
                    constraints.setHorizontalWeight(it, triple.first.size.toFloat())
                    (view?.findViewById<View?>(it) as HandImageWithTextListView).let {view ->
                        val agari =
                            if (index == subScoreHelper.agariPlace.first) subScoreHelper.agariPlace.second
                            else null
                        view.setHandImageWithText(act.applicationContext, triple.first, agari)
                        val back = if (triple.third == SubScoreDisplayHelper.Companion.STATUS.NAKI)
                            Color.BLUE else  Color.YELLOW
                        view.setBackgroundColor(back)
                    }
                }
                getFieldId("subScoreText${index+1}")?.let {
                    (view?.findViewById<View?>(it) as TextView).let {view ->
                        view.text = triple.second.toString()
                    }
                }
            }
            constraints.applyTo(handDetailLayout)
            val adapter = SubScoreDetailAdapter(act.applicationContext)
            adapter.handInfo = subScoreHelper.createResultTextList()
            handList2.adapter = adapter
        }
    }

    private fun setHandValueDetailText() {
        (activity as ResultActivity).let {
            textHandValueDetail.text = (it.findViewById<View>(R.id.scoreText) as TextView).text
        }
    }

    private fun setHandDetailText() {
        (activity as ResultActivity).let {
            val items = when (Variables.isTsumo) {
                true -> it.hand.tsumoHandNameAndValue()
                false -> it.hand.ronHandNameAndValue()
            }
            val splitter = items.size
            val adapter1 = HandDetailAdapter(it.applicationContext)
            adapter1.handInfo = items.subList(0, splitter)
            handList1.adapter = adapter1
//            val adapter2 = HandDetailAdapter(it.applicationContext)
//            adapter2.handInfo = items.subList(splitter, items.size)
//            handList2.adapter = adapter2
        }
    }

//    // TODO: Rename method, update argument and hook method into UI event
//    fun onButtonPressed(uri: Uri) {
//        listener?.onFragmentInteraction(uri)
//    }

//    override fun onAttach(context: Context) {
//        super.onAttach(context)
//        if (context is OnFragmentInteractionListener) {
//            listener = context
//        } else {
//            throw RuntimeException(context.toString() + " must implement OnFragmentInteractionListener")
//        }
//    }

//    override fun onDetach() {
//        super.onDetach()
//        listener = null
//    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     *
     *
     * See the Android Training lesson [Communicating with Other Fragments]
     * (http://developer.android.com/training/basics/fragments/communicating.html)
     * for more information.
     */
//    interface OnFragmentInteractionListener {
//        // TODO: Update argument type and name
//        fun onFragmentInteraction(uri: Uri)
//    }

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment ResultDetailFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            ResultDetailFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }
}
