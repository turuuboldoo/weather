package mn.turbo.weather.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.google.gson.annotations.SerializedName

@Entity("weather")
data class WeatherEntity(
    @SerializedName("time")
    val time: List<String>,

    @SerializedName("temperature_2m")
    val temperatures: List<Double>,

    @SerializedName("weathercode")
    val weatherCodes: List<Int>,

    @SerializedName("pressure_msl")
    val pressures: List<Double>,

    @SerializedName("windspeed_10m")
    val windSpeeds: List<Double>,

    @SerializedName("relativehumidity_2m")
    val humidities: List<Double>,

    @PrimaryKey(autoGenerate = true)
    val id: Int?
)
