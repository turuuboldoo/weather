package mn.turbo.weather.data.mappers

import mn.turbo.weather.data.local.entity.WeatherEntity
import mn.turbo.weather.data.remote.dto.WeatherDataDto
import mn.turbo.weather.data.remote.dto.WeatherDto
import mn.turbo.weather.domain.weather.WeatherData
import mn.turbo.weather.domain.weather.WeatherInfo
import mn.turbo.weather.domain.weather.WeatherType
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

private data class IndexedWeatherData(
    val index: Int,
    val data: WeatherData
)

fun WeatherDataDto.toWeatherDataMap(): Map<Int, List<WeatherData>> {
    return time.mapIndexed { index, time ->
        val temperature = temperatures[index]
        val weatherCode = weatherCodes[index]
        val windSpeed = windSpeeds[index]
        val pressure = pressures[index]
        val humidity = humidities[index]

        IndexedWeatherData(
            index = index,
            data = WeatherData(
                time = LocalDateTime.parse(time, DateTimeFormatter.ISO_DATE_TIME),
                temperatureCelsius = temperature,
                pressure = pressure,
                windSpeed = windSpeed,
                humidity = humidity,
                weatherType = WeatherType.fromWMO(weatherCode)
            )
        )
    }.groupBy { indexedWeatherData ->
        indexedWeatherData.index / 24
    }.mapValues { mappedData ->
        mappedData.value.map { it.data }
    }
}

fun WeatherDto.toWeatherInfo(): WeatherInfo {
    val weatherDataMap = weatherData.toWeatherDataMap()
    val now = LocalDateTime.now()
    val currentWeatherData = weatherDataMap[0]?.find {
        val hour = if (now.minute < 30) now.hour else now.hour + 1
        it.time.hour == hour
    }

    return WeatherInfo(
        weatherDataPerDay = weatherDataMap,
        currentWeatherData = currentWeatherData
    )
}

fun WeatherDataDto.toWeatherEntity(): WeatherEntity {
    return WeatherEntity(
        time = time,
        temperatures = temperatures,
        weatherCodes = weatherCodes,
        pressures = pressures,
        windSpeeds = windSpeeds,
        humidities = humidities,
        id = null
    )
}

fun WeatherEntity.toWeatherDto(): WeatherDto {
    return WeatherDto(
        weatherData = WeatherDataDto(
            time = time,
            temperatures = temperatures,
            weatherCodes = weatherCodes,
            pressures = pressures,
            windSpeeds = windSpeeds,
            humidities = humidities
        )
    )
}
