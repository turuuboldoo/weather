package mn.turbo.weather.data.local

import androidx.room.RoomDatabase
import androidx.room.TypeConverters

//@Database(
//    entities = [
//
//    ],
//    version = 1
//)
@TypeConverters(DataConverter::class)
abstract class WeatherDatabase : RoomDatabase() {
}
