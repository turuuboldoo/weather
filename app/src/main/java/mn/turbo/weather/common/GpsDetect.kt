package mn.turbo.weather.common

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import androidx.core.app.ActivityCompat
import com.google.android.gms.location.Granularity
import com.google.android.gms.location.LocationAvailability
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.Priority

class GpsDetect(
    private val context: Context
) {

    private val fusedLocationProviderClient = LocationServices
        .getFusedLocationProviderClient(context)
    private var locationCallback: LocationCallback? = null

    fun detectGPS(onGPSChanged: (Boolean) -> Unit = {}) {
        locationCallback = object : LocationCallback() {
            override fun onLocationAvailability(p0: LocationAvailability) {
                super.onLocationAvailability(p0)
                onGPSChanged(p0.isLocationAvailable)
            }
        }

        if (ActivityCompat.checkSelfPermission(
                context,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                context,
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            onGPSChanged(false)
            return
        }

        val mLocationRequest = LocationRequest.Builder(
            Priority.PRIORITY_HIGH_ACCURACY,
            5000
        ).apply {
            setMinUpdateDistanceMeters(5F)
            setGranularity(Granularity.GRANULARITY_PERMISSION_LEVEL)
            setWaitForAccurateLocation(true)
        }.build()

        fusedLocationProviderClient.requestLocationUpdates(
            mLocationRequest,
            locationCallback as LocationCallback,
            null
        )
    }

    fun stop() {
        locationCallback?.let { fusedLocationProviderClient.removeLocationUpdates(it) }
    }
}
