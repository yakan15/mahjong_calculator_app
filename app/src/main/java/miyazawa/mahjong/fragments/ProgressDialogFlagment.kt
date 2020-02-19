package miyazawa.mahjong.fragments

import android.app.AlertDialog
import android.app.Dialog
import android.content.DialogInterface
import android.os.Bundle
import androidx.fragment.app.DialogFragment
import miyazawa.mahjong.R
import java.lang.IllegalStateException

class ProgressDialogFragment: DialogFragment() {
    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
       return activity?.let {
           val builder = AlertDialog.Builder(it)
           builder.setMessage(R.string.sending)
               .setTitle(R.string.title_send_alert)
               .setNegativeButton(
                   R.string.cancel,
                   DialogInterface.OnClickListener {dialog, id ->
                       getDialog()?.cancel()
               })
               .create()
       } ?: throw IllegalStateException("Activity cannot be null")
    }
}