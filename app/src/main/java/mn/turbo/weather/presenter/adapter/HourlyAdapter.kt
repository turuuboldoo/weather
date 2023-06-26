package mn.turbo.weather.presenter.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import mn.turbo.weather.databinding.ItemHourlyBinding
import mn.turbo.weather.domain.weather.WeatherData

class HourlyAdapter : ListAdapter<WeatherData, HourlyAdapter.ViewHolder>(weatherDataDiffUtil) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding: ItemHourlyBinding = ItemHourlyBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )

        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        getItem(position)?.let {
            holder.binding.run {
                mImageViewIcon.setImageDrawable(
                    ContextCompat.getDrawable(
                        holder.itemView.context,
                        it.weatherType.iconRes
                    )
                )

                mTextViewTime.text = it.getFormattedTime()
                mTextViewTemperature.text = String.format("%.2f°C", it.temperatureCelsius)
            }
        }
    }

    inner class ViewHolder(
        val binding: ItemHourlyBinding
    ) : RecyclerView.ViewHolder(binding.root)
}

private val weatherDataDiffUtil = object : DiffUtil.ItemCallback<WeatherData>() {
    override fun areItemsTheSame(oldItem: WeatherData, newItem: WeatherData) =
        oldItem == newItem

    override fun areContentsTheSame(oldItem: WeatherData, newItem: WeatherData) =
        oldItem.time == newItem.time

}