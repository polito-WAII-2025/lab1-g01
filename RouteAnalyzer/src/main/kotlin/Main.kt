package it.polito.wa2.g01

import org.apache.commons.csv.CSVFormat
import org.apache.commons.csv.CSVParser
import java.io.FileReader
import kotlin.math.*


// Definiamo la classe Waypoint per rappresentare ogni punto del percorso
data class Waypoint(val timestamp: Long, val latitude: Double, val longitude: Double)

fun haversine(lat1: Double, lon1: Double, lat2: Double, lon2: Double): Double {
    val R = 6371.0 // Raggio della Terra in km
    val dLat = Math.toRadians(lat2 - lat1)
    val dLon = Math.toRadians(lon2 - lon1)
    val a = sin(dLat / 2) * sin(dLat / 2) +
            cos(Math.toRadians(lat1)) * cos(Math.toRadians(lat2)) *
            sin(dLon / 2) * sin(dLon / 2)
    val c = 2 * atan2(sqrt(a), sqrt(1 - a))
    return R * c
}

fun main() {
    val waypoints = mutableListOf<Waypoint>()


    val reader = FileReader("RouteAnalyzer/src/main/resources/waypoints.csv")
    val csvParser = CSVParser(reader, CSVFormat.DEFAULT.withDelimiter(';'))

    // Itera attraverso le righe del CSV e popola la lista di Waypoint
    for (csvRecord in csvParser) {
        val timestamp = csvRecord.get(0).toDoubleOrNull()?.toLong()
        val latitude = csvRecord.get(1).toDoubleOrNull()
        val longitude= csvRecord.get(2).toDoubleOrNull()

        if (timestamp != null && latitude != null && longitude != null) {
            waypoints.add(Waypoint(timestamp, latitude, longitude))
        }
    }

    val startPoint = waypoints.first()
    var maxDistance = 0.0
    var totalDistance = 0.0

    for (i in 1 until waypoints.size) {
        val prev = waypoints[i - 1]
        val curr = waypoints[i]
        val segmentDistance = haversine(prev.latitude, prev.longitude, curr.latitude, curr.longitude)

        totalDistance += segmentDistance

        val distanceFromStart = haversine(startPoint.latitude, startPoint.longitude, curr.latitude, curr.longitude)
        if (distanceFromStart > maxDistance) {
            maxDistance = distanceFromStart
        }
    }

    println("Distanza massima dal punto di partenza: ${"%.3f".format(maxDistance)} km")
    println("Distanza totale del percorso: ${"%.3f".format(totalDistance)} km")
}