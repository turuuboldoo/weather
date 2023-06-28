package mn.turbo.weather.domain.repository

import mn.turbo.weather.common.Resource
import mn.turbo.weather.domain.weather.WeatherInfo

interface WeatherRepository {
    suspend fun getWeatherData(
        lat: Double,
        long: Double,
    ): Resource<WeatherInfo>
}
