package mn.turbo.weather.data.repository

import android.content.Context
import android.net.ConnectivityManager
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
    private val context: Context,
    private val api: WeatherApi,
    private val dao: WeatherDao,
    @IoDispatcher private val ioDispatcher: CoroutineDispatcher,
) : WeatherRepository {

    override suspend fun getWeatherData(
        lat: Double,
        long: Double,
    ): Resource<WeatherInfo> {
        return try {
            if (isNetworkAvailable(context)) {
                val result = api.getWeather(lat = lat, long = long)

                withContext(ioDispatcher) {
                    dao.insert(result.weatherData.toWeatherEntity())
                }

                Resource.Success(
                    data = result.toWeatherInfo()
                )
            } else {
                Resource.Success(
                    data = dao.select()
                        .last()
                        .toWeatherDto()
                        .toWeatherInfo()
                )
            }
        } catch (e: Exception) {
            e.printStackTrace()
            Resource.Error(e.message ?: "Something goes wrong!")
        }
    }

    private fun isNetworkAvailable(context: Context): Boolean {
        val connectivityManager =
            context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager?
        val activeNetworkInfo = connectivityManager?.activeNetworkInfo
        return activeNetworkInfo != null && activeNetworkInfo.isConnected
    }
}
