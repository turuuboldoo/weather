package mn.turbo.weather.di

import android.app.Application
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.CoroutineDispatcher
import mn.turbo.weather.data.local.dao.WeatherDao
import mn.turbo.weather.data.remote.WeatherApi
import mn.turbo.weather.data.repository.WeatherRepositoryImpl
import mn.turbo.weather.domain.repository.WeatherRepository
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object RepositoryModule {

    @Provides
    @Singleton
    fun bindWeatherRepository(
        application: Application,
        dao: WeatherDao,
        api: WeatherApi,
        @IoDispatcher ioDispatcher: CoroutineDispatcher,
    ): WeatherRepository {
        return WeatherRepositoryImpl(
            application,
            api,
            dao,
            ioDispatcher,
        )
    }

}
