package mn.turbo.weather.data.repository

import mn.turbo.weather.common.Resource
import mn.turbo.weather.data.mappers.toWeatherINfo
import mn.turbo.weather.data.remote.WeatherApi
import mn.turbo.weather.domain.repository.WeatherRepository
import mn.turbo.weather.domain.weather.WeatherInfo
import java.lang.Exception
import javax.inject.Inject

class WeatherRepositoryImpl @Inject constructor(
    private val api: WeatherApi
) : WeatherRepository {

    override suspend fun getWeatherData(
        lat: Double,
        long: Double
    ): Resource<WeatherInfo> {
        return try {
            Resource.Success(
                data = api.getWeather(
                    lat = lat,
                    long = long
                ).toWeatherINfo()
            )
        } catch (e: Exception) {
            e.printStackTrace()
            Resource.Error(e.message ?: "Something goes wrong!")
        }
    }

}
