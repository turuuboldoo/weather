package mn.turbo.weather.di

import android.app.Application
import androidx.room.Room
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import mn.turbo.weather.common.Constant
import mn.turbo.weather.data.local.WeatherDatabase
import mn.turbo.weather.data.local.dao.WeatherDao
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    @Provides
    @Singleton
    fun provideDatabase(
        app: Application,
    ): WeatherDatabase {
        return Room.databaseBuilder(
            app.applicationContext,
            WeatherDatabase::class.java,
            Constant.DATABASE_NAME
        ).build()
    }

    @Provides
    @Singleton
    fun provideMovieDao(
        db: WeatherDatabase,
    ): WeatherDao {
        return db.weatherDao
    }

}
