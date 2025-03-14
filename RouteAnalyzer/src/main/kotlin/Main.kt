package it.polito.wa2.g01

import org.apache.commons.csv.CSVFormat
import org.apache.commons.csv.CSVParser
import java.io.FileReader
import kotlin.math.*


// Waypoint class: define every path point
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

fun findMostFrequentedAreaGrid(waypoints: List<Waypoint>, cellSizeKm: Double): Pair<Waypoint, Int> {
    // Constants
    val kmPerDegreeLatitude = 111.0 // Approximate conversion factor from kilometers to degrees (latitude)

    // Convert cell size in kilometers to degrees (approximate)
    val cellSizeDegLatitude = cellSizeKm / kmPerDegreeLatitude
    val cellSizeDegLongitude = cellSizeKm / (kmPerDegreeLatitude * Math.cos(Math.toRadians(waypoints.first().latitude))) // Adjust for longitude scale

    // Create a map to track the waypoints in each grid cell
    val grid = mutableMapOf<Pair<Int, Int>, MutableList<Waypoint>>()

    // Iterate through each waypoint and assign it to the corresponding grid cell
    for (w in waypoints) {
        // Calculate the grid cell for the waypoint using its latitude and longitude
        val cellX = (w.latitude / cellSizeDegLatitude).toInt()
        val cellY = (w.longitude / cellSizeDegLongitude).toInt()

        // Create a new list for the cell if it doesn't already exist, and add the waypoint
        val cell = cellX to cellY //creates a pair (cellX, cellY)
        grid.computeIfAbsent(cell) { mutableListOf() }.add(w)
    }

    // Find the grid cell with the most waypoints
    val (bestCell, bestWaypoints) = grid.maxByOrNull { it.value.size } ?: return waypoints.first() to 0

    // Choose the central waypoint of the most frequent cell (based on the number of waypoints)
    val centralWaypoint = bestWaypoints[bestWaypoints.size / 2]  // Get the central waypoint in the list

    // Return the central waypoint and the number of waypoints in the most populated grid cell
    return centralWaypoint to bestWaypoints.size
}



fun main() {
    val waypoints = mutableListOf<Waypoint>()


    val reader = FileReader("RouteAnalyzer/src/main/resources/waypoints.csv")
    val csvParser = CSVParser(reader, CSVFormat.DEFAULT.withDelimiter(';'))

    for (csvRecord in csvParser) {
        val timestamp = csvRecord.get(0).toDoubleOrNull()?.toLong()
        val latitude = csvRecord.get(1).toDoubleOrNull()
        val longitude= csvRecord.get(2).toDoubleOrNull()

        if (timestamp != null && latitude != null && longitude != null) {
            waypoints.add(Waypoint(timestamp, latitude, longitude))
        }
    }

    // Function 1: maxDistanceFromStart
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


    // Function 2: mostFrequentedArea
    val resulted_area = findMostFrequentedAreaGrid(waypoints, 0.5)
    println(resulted_area)



    // Function 3: waypointsOutsideGeofence
}