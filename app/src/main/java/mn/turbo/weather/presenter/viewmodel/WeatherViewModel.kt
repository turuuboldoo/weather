package mn.turbo.weather.presenter.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import mn.turbo.weather.common.Resource
import mn.turbo.weather.common.UiState
import mn.turbo.weather.domain.location.LocationTracker
import mn.turbo.weather.domain.repository.WeatherRepository
import mn.turbo.weather.domain.weather.WeatherInfo
import javax.inject.Inject

@HiltViewModel
class WeatherViewModel @Inject constructor(
    private val repository: WeatherRepository,
    private val locationTracker: LocationTracker
) : ViewModel() {

    private var _weatherInfoState = MutableStateFlow(UiState<WeatherInfo>(isLoading = true))
    val weatherInfoState = _weatherInfoState.asStateFlow()

    fun loadWeatherInfo(isRefresh: Boolean = false) =
        viewModelScope.launch {
            _weatherInfoState.value = UiState(
                isLoading = true,
                data = null,
            )

            locationTracker.getCurrentLocation()?.let { location ->
                when (val result =
                    repository.getWeatherData(location.latitude, location.longitude, isRefresh)) {
                    is Resource.Success -> {
                        _weatherInfoState.value = UiState(
                            data = result.data,
                            isLoading = false,
                        )
                    }

                    is Resource.Loading -> {
                        _weatherInfoState.value = UiState(
                            isLoading = true,
                            data = null,
                        )
                    }

                    is Resource.Error -> {
                        _weatherInfoState.value = UiState(
                            isLoading = false,
                            error = result.message ?: "Something goes wrong",
                            data = null
                        )
                    }
                }
            } ?: kotlin.run {
                _weatherInfoState.value = UiState(
                    isLoading = false,
                    error = "Something goes wrong",
                    data = null,
                )
            }
        }
}
