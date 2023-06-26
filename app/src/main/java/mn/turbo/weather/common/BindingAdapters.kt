package mn.turbo.weather.common

import android.view.View
import androidx.databinding.BindingAdapter

@BindingAdapter(value = ["isVisible"], requireAll = false)
fun isVisible(view: View, boolean: Boolean) {
    view.visibility = if (boolean) View.GONE else View.VISIBLE
}
