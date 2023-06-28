package mn.turbo.weather.common.dialog

import android.app.AlertDialog
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.view.View

abstract class BaseDialog {

    abstract val mDialogView: View
    abstract val mBuilder: AlertDialog.Builder

    //  required boolean
    open var cancelable: Boolean = true
    open var isBackGroundTransparent: Boolean = true

    //  dialog
    open var mDialog: AlertDialog? = null

    open fun create(): AlertDialog {
        mDialog = mBuilder
            .setCancelable(cancelable)
            .create()

        if (isBackGroundTransparent)
            mDialog?.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))

        return mDialog!!
    }

    //  cancel listener
    open fun onCancelListener(func: () -> Unit): AlertDialog.Builder? =
        mBuilder.setOnCancelListener {
            func()
        }

    open fun onDismissListener(func: () -> Unit): AlertDialog.Builder? =
        mBuilder.setOnDismissListener {
            func()
        }
}
