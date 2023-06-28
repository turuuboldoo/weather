package mn.turbo.weather.common.dialog

import android.app.Activity
import android.app.AlertDialog
import androidx.fragment.app.Fragment

inline fun Activity.showWarning(func: WarningDialog.() -> Unit): AlertDialog =
    WarningDialog(this).apply { func() }.create()

inline fun Fragment.showWarning(func: WarningDialog.() -> Unit): AlertDialog =
    WarningDialog(requireContext()).apply { func() }.create()
