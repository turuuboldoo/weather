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
import mn.turbo.weather.R
import mn.turbo.weather.common.ConnectivityObserver
import mn.turbo.weather.common.GpsDetect
import mn.turbo.weather.common.UiState
import mn.turbo.weather.common.dialog.showWarning
import mn.turbo.weather.common.extension.collectLatestLifecycleFlow
import mn.turbo.weather.databinding.FragmentMainBinding
import mn.turbo.weather.domain.weather.WeatherInfo
import mn.turbo.weather.presenter.adapter.HourlyAdapter
import mn.turbo.weather.presenter.viewmodel.WeatherViewModel
import javax.inject.Inject
import kotlin.math.roundToInt

@AndroidEntryPoint
class MainFragment : Fragment() {

    @Inject
    lateinit var connectivityObserver: ConnectivityObserver

    private var _binding: FragmentMainBinding? = null
    private val binding: FragmentMainBinding get() = _binding!!

    private val viewModel: WeatherViewModel by viewModels()

    private lateinit var hourlyAdapter: HourlyAdapter

    private lateinit var gpsDetect: GpsDetect
    private lateinit var permissionLauncher: ActivityResultLauncher<Array<String>>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        permissionLauncher = registerForActivityResult(
            ActivityResultContracts.RequestMultiplePermissions()
        ) {
            gpsDetect.detectGPS { isGpsChanged ->
                if (isGpsChanged) {
                    viewModel.loadWeatherInfo()
                } else {
                    showWarning {
                        setDesc(getString(R.string.gps_warning))
                        setButtonText(getString(R.string.ok))

                        onClose {

                        }
                    }.show()
                }
            }
        }

        permissionLauncher.launch(
            arrayOf(
                Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.ACCESS_COARSE_LOCATION,
            )
        )

        gpsDetect = GpsDetect(requireContext())
    }

    override fun onStop() {
        super.onStop()
        gpsDetect.stop()
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
        hourlyAdapter = HourlyAdapter()

        collectLatestLifecycleFlow(viewModel.weatherInfoState) { uiState ->
            if (!uiState.isLoading) {
                binding.run {
                    mProgressBar.visibility = View.GONE
                }
            }

            setUI(uiState)

            hourlyAdapter.submitList(uiState.data?.weatherDataPerDay?.get(0))
        }

        collectLatestLifecycleFlow(connectivityObserver.observe()) {
            when (it) {
                ConnectivityObserver.Status.Available -> {
                    viewModel.loadWeatherInfo()
                }

                else -> {
                    showWarning {
                        setDesc(getString(R.string.no_internet))
                        setButtonText(getString(R.string.ok))

                        onClose {

                        }
                    }.show()
                }
            }
        }
    }

    private fun setUI(uiState: UiState<WeatherInfo>) {
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
    }
}
