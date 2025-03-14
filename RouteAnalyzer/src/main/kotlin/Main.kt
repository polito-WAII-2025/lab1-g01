package it.polito.wa2.g01

import kotlinx.serialization.ExperimentalSerializationApi
import org.apache.commons.csv.CSVFormat
import org.apache.commons.csv.CSVParser
import kotlinx.serialization.json.Json
import kotlinx.serialization.encodeToString
import java.io.File
import java.io.FileReader
import com.uber.h3core.H3Core



fun maxDistanceFromStart(waypoints: List<Waypoint>): Pair<Waypoint, Double>? {
    if (waypoints.isEmpty()) return null

    val start = waypoints.first()
    return waypoints.maxByOrNull { start.distanceTo(it) }
        ?.let { it to start.distanceTo(it) }
}
/*
fun mostFrequentedArea(waypoints: List<Waypoint>, areaRadiusKm: Double): Pair<Waypoint, Int>? {
    if (waypoints.isEmpty()) return null

    return waypoints.maxByOrNull { center ->
        waypoints.count { center.distanceTo(it) <= areaRadiusKm }
    }?.let { center -> center to waypoints.count { center.distanceTo(it) <= areaRadiusKm } }
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
*/

//Un geofence è un cerchio con centro fisso e raggio noto
fun waypointsOutsideGeofence(waypoints: List<Waypoint>, center: Waypoint, geofenceRadiusKm: Double): List<Waypoint> {
    return waypoints.filter { it.distanceTo(center) > geofenceRadiusKm }
}

@OptIn(ExperimentalSerializationApi::class)
fun saveResultsToJson(
    maxDistance: Pair<Waypoint, Double>?,
    frequentArea: Pair<Waypoint, Int>?,
    outsideGeofence: List<Waypoint>,
    geofenceCenter: Waypoint,
    geofenceRadius: Double,
    areaRadius: Double
) {
    val output = OutputData(
        maxDistanceFromStart = maxDistance?.let { MaxDistanceData(it.first, it.second) } ?: error("No data"),
        mostFrequentedArea = frequentArea?.let { FrequentedAreaData(it.first, areaRadius, it.second) } ?: error("No data"),
        waypointsOutsideGeofence = GeoFenceData(geofenceCenter, geofenceRadius, outsideGeofence.size, outsideGeofence)
    )


    val json = Json {
        prettyPrint = true // Abilita la formattazione leggibile
        prettyPrintIndent = "  " // Indentazione di 2 spazi (puoi cambiarla)
    }

    val jsonString = json.encodeToString(output)
    File("evaluation/output.json").writeText(jsonString)
}


fun mostFrequentedArea(waypoints: List<Waypoint>, resolution: Int = 9): Pair<Waypoint, Int>? {
    if (waypoints.isEmpty()) return null

    val h3 = H3Core.newInstance()
    val cellFrequency = mutableMapOf<String, Int>()

    // Conta il numero di waypoint per ogni cella H3
    for (wp in waypoints) {
        val h3Index = h3.geoToH3Address(wp.latitude, wp.longitude, resolution)
        cellFrequency[h3Index] = cellFrequency.getOrDefault(h3Index, 0) + 1
    }

    // Trova la cella più frequentata
    val mostFrequentCell = cellFrequency.maxByOrNull { it.value } ?: return null

    // Converte la cella H3 in coordinate geografiche
    val center = h3.h3ToGeo(mostFrequentCell.key)
    return Waypoint(0, center.lat, center.lng) to mostFrequentCell.value
}






fun main() {

    // Configuration data
    val config = ConfigLoader.load("evaluation/custom-parameters.yml")

    println("Earth Radius: ${config.earthRadiusKm} km")
    println("Geofence Center: (${config.geofenceCenterLatitude}, ${config.geofenceCenterLongitude})")
    println("Geofence Radius: ${config.geofenceRadiusKm} km")
    println("Most Frequented Area Radius: ${config.mostFrequentedAreaRadiusKm ?: "Not Provided"} km")


    val waypoints = mutableListOf<Waypoint>()


    val reader = FileReader("evaluation/waypoints.csv")
    val csvParser = CSVParser(reader, CSVFormat.DEFAULT.builder()
        .setDelimiter(';')
        .build())

    // Itera attraverso le righe del CSV e popola la lista di Waypoint
    for (csvRecord in csvParser) {
        val timestamp = csvRecord.get(0).toDoubleOrNull()?.toLong()
        val latitude = csvRecord.get(1).toDoubleOrNull()
        val longitude= csvRecord.get(2).toDoubleOrNull()

        if (timestamp != null && latitude != null && longitude != null) {
            waypoints.add(Waypoint(timestamp, latitude, longitude))
        }
    }

    csvParser.close()

    val maxDistance = maxDistanceFromStart(waypoints)
    val frequentArea = mostFrequentedArea(waypoints)  // Raggio di 0.5 km
    val geofenceCenter = Waypoint(0, 45.0, 41.0)  // Valori esempio, da custom-parameters.yml
    val geofenceRadius = 0.4  // Valore esempio
    val outsideGeofence = waypointsOutsideGeofence(waypoints, geofenceCenter, geofenceRadius)
    println(frequentArea)
    saveResultsToJson(maxDistance, frequentArea, outsideGeofence, geofenceCenter, geofenceRadius, 0.5)
}