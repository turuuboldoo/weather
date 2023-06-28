package mn.turbo.weather.common.dialog

import android.app.AlertDialog
import android.content.Context
import android.view.View
import android.widget.Button
import android.widget.TextView
import mn.turbo.weather.R

class WarningDialog(
    context: Context
) : BaseDialog() {

    override val mDialogView: View by lazy {
        View.inflate(context, R.layout.dialog_warning, null)
    }

    override val mBuilder: AlertDialog.Builder = AlertDialog.Builder(context).setView(mDialogView)

    private val mTextView: TextView by lazy {
        mDialogView.findViewById(R.id.mTextViewDesc)
    }

    private val mButton: Button by lazy {
        mDialogView.findViewById(R.id.mButton)
    }

    fun setDesc(string: String?) = with(mTextView) {
        this.text = string
    }

    fun setButtonText(string: String) = with(mButton) {
        this.text = string
    }

    fun onClose(func: (() -> Unit)?) = with(mButton) {
        setOptionClickLister(func)
    }

    private fun View.setOptionClickLister(func: (() -> Unit)?) {
        setOnClickListener {
            func?.invoke()
            mDialog?.dismiss()
        }
    }
}