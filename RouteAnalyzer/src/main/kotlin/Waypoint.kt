package it.polito.wa2.g01

import kotlinx.serialization.Serializable
import kotlin.math.*

// Defining Waypoint class for representing every point of the path
@Serializable
data class Waypoint(val timestamp: Long, val latitude: Double, val longitude: Double) {

    //Function to calculate the distance between two waypoints (using Haversine formula)
    fun distanceTo(other: Waypoint, earthRadiusKm: Double): Double {
        val dLat = Math.toRadians(other.latitude - latitude)
        val dLon = Math.toRadians(other.longitude - longitude)
        val a = sin(dLat / 2).pow(2) +
                cos(Math.toRadians(latitude)) * cos(Math.toRadians(other.latitude)) *
                sin(dLon / 2).pow(2)
        val c = 2 * atan2(sqrt(a), sqrt(1 - a))
        return earthRadiusKm * c
    }
}

