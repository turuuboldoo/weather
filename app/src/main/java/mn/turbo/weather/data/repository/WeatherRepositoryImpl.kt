package mn.turbo.weather.data.repository

import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.withContext
import mn.turbo.weather.common.Resource
import mn.turbo.weather.data.local.dao.WeatherDao
import mn.turbo.weather.data.mappers.toWeatherDto
import mn.turbo.weather.data.mappers.toWeatherEntity
import mn.turbo.weather.data.mappers.toWeatherInfo
import mn.turbo.weather.data.remote.WeatherApi
import mn.turbo.weather.di.IoDispatcher
import mn.turbo.weather.domain.repository.WeatherRepository
import mn.turbo.weather.domain.weather.WeatherInfo
import javax.inject.Inject

class WeatherRepositoryImpl @Inject constructor(
    private val api: WeatherApi,
    private val dao: WeatherDao,
    @IoDispatcher private val ioDispatcher: CoroutineDispatcher
) : WeatherRepository {

    override suspend fun getWeatherData(
        lat: Double,
        long: Double
    ): Resource<WeatherInfo> {
        return try {
            val weatherDto = dao.select()?.toWeatherDto()

            if (weatherDto == null) {
                val result = api.getWeather(lat = lat, long = long)

                withContext(ioDispatcher) {
                    dao.insert(result.weatherData.toWeatherEntity())
                }

                Resource.Success(
                    data = result.toWeatherInfo()
                )
            } else {
                Resource.Success(
                    data = weatherDto.toWeatherInfo()
                )
            }
        } catch (e: Exception) {
            e.printStackTrace()
            Resource.Error(e.message ?: "Something goes wrong!")
        }
    }

}
