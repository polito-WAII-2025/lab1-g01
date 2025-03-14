package it.polito.wa2.g01

import kotlinx.serialization.Serializable
import kotlin.math.*

// Definiamo la classe Waypoint per rappresentare ogni punto del percorso
@Serializable
data class Waypoint(val timestamp: Long, val latitude: Double, val longitude: Double) {
    fun distanceTo(other: Waypoint): Double {
        val earthRadiusKm = 6371.0
        val dLat = Math.toRadians(other.latitude - latitude)
        val dLon = Math.toRadians(other.longitude - longitude)
        val a = sin(dLat / 2).pow(2) +
                cos(Math.toRadians(latitude)) * cos(Math.toRadians(other.latitude)) *
                sin(dLon / 2).pow(2)
        val c = 2 * atan2(sqrt(a), sqrt(1 - a))
        return earthRadiusKm * c
    }
}

