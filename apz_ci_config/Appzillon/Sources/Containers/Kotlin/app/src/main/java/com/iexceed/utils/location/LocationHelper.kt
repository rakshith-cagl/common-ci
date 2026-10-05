package com.iexceed.utils.location

import android.location.Address
import android.location.Geocoder
import android.location.Location
import kotlin.math.abs
import kotlin.math.roundToInt

object LocationHelper {
    fun getCurrentLocationFromGeocode(
        geocoder: Geocoder,
        presentLocation: Location
    ) =
        geocoder.getFromLocation(
            presentLocation.latitude,
            presentLocation.longitude,
            1
        ) as List<Address>

    fun getFormattedLocationInDegree(latitude: Double, longitude: Double): String? {
        return try {
            var latSeconds = (latitude * 3600).roundToInt()
            val latDegrees = latSeconds / 3600
            latSeconds = abs(latSeconds % 3600)
            val latMinutes = latSeconds / 60
            latSeconds %= 60
            var longSeconds = (longitude * 3600).roundToInt()
            val longDegrees = longSeconds / 3600
            longSeconds = Math.abs(longSeconds % 3600)
            val longMinutes = longSeconds / 60
            longSeconds %= 60
            val latDegree = if (latDegrees >= 0) "N" else "S"
            val lonDegrees = if (longDegrees >= 0) "E" else "W"
            (abs(latDegrees).toString() + "° " + latMinutes + "' " + latSeconds
                    + "\" " + latDegree + ";" + abs(longDegrees) + "° " + longMinutes
                    + "' " + longSeconds + "\" " + lonDegrees)
        } catch (e: Exception) {
            ""
        }
    }
}