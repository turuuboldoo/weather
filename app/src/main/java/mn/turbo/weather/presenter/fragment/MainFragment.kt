package mn.turbo.weather.presenter.fragment

import android.Manifest
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import dagger.hilt.android.AndroidEntryPoint
import mn.turbo.weather.common.extension.collectLatestLifecycleFlow
import mn.turbo.weather.databinding.FragmentMainBinding
import mn.turbo.weather.presenter.adapter.HourlyAdapter
import mn.turbo.weather.presenter.viewmodel.WeatherViewModel
import kotlin.math.roundToInt

@AndroidEntryPoint
class MainFragment : Fragment() {

    private var _binding: FragmentMainBinding? = null
    private val binding: FragmentMainBinding get() = _binding!!

    private val viewModel: WeatherViewModel by viewModels()

    private lateinit var permissionLauncher: ActivityResultLauncher<Array<String>>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        permissionLauncher = registerForActivityResult(
            ActivityResultContracts.RequestMultiplePermissions()
        ) {
            viewModel.loadWeatherInfo()
        }
        permissionLauncher.launch(
            arrayOf(
                Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.ACCESS_COARSE_LOCATION,
            )
        )
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentMainBinding.inflate(
            inflater,
            container,
            false
        )

        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun onViewCreated(
        view: View,
        savedInstanceState: Bundle?
    ) {
        super.onViewCreated(view, savedInstanceState)
        val hourlyAdapter = HourlyAdapter()

        collectLatestLifecycleFlow(viewModel.weatherInfoState) { uiState ->
            if (!uiState.isLoading) {
                binding.mProgressBar.visibility = View.GONE
            }

            binding.apply {
                mRecyclerView.adapter = hourlyAdapter

                mImageViewStatus.setImageDrawable(
                    uiState.data?.currentWeatherData?.weatherType?.iconRes?.let {
                        ContextCompat.getDrawable(
                            requireContext(),
                            it
                        )
                    }
                )

                mTextViewDesc.text = uiState.data?.currentWeatherData?.weatherType?.weatherDesc
                mTextViewTime.text =
                    String.format(
                        "Today %s",
                        uiState.data?.currentWeatherData?.getFormattedTime() ?: ""
                    )
                mTextViewTemperature.text =
                    String.format(
                        "%.2f°C",
                        uiState.data?.currentWeatherData?.temperatureCelsius ?: 0.0
                    )
                mTextViewPressure.text =
                    String.format(
                        "%d",
                        uiState.data?.currentWeatherData?.pressure?.roundToInt() ?: 0
                    )
                mTextViewHumidity.text =
                    String.format(
                        "%d",
                        uiState.data?.currentWeatherData?.humidity?.roundToInt() ?: 0
                    )
                mTextViewWind.text =
                    String.format(
                        "%d",
                        uiState.data?.currentWeatherData?.windSpeed?.roundToInt() ?: 0
                    )
            }

            hourlyAdapter.submitList(uiState.data?.weatherDataPerDay?.get(0))
        }
    }
}
