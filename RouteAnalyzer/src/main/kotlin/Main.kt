package it.polito.wa2.g01

import org.apache.commons.csv.CSVFormat
import org.apache.commons.csv.CSVParser
import kotlinx.serialization.json.Json
import kotlinx.serialization.encodeToString
import java.io.File
import java.io.FileReader

fun maxDistanceFromStart(waypoints: List<Waypoint>): Pair<Waypoint, Double>? {
    if (waypoints.isEmpty()) return null

    val start = waypoints.first()
    return waypoints.maxByOrNull { start.distanceTo(it) }
        ?.let { it to start.distanceTo(it) }
}

fun mostFrequentedArea(waypoints: List<Waypoint>, areaRadiusKm: Double): Pair<Waypoint, Int>? {
    if (waypoints.isEmpty()) return null

    return waypoints.maxByOrNull { center ->
        waypoints.count { center.distanceTo(it) <= areaRadiusKm }
    }?.let { center -> center to waypoints.count { center.distanceTo(it) <= areaRadiusKm } }
}

//Un geofence è un cerchio con centro fisso e raggio noto
fun waypointsOutsideGeofence(waypoints: List<Waypoint>, center: Waypoint, geofenceRadiusKm: Double): List<Waypoint> {
    return waypoints.filter { it.distanceTo(center) > geofenceRadiusKm }
}

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

    val jsonString = Json.encodeToString(output)
    File("RouteAnalyzer/src/main/resources/output.json").writeText(jsonString)
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

    csvParser.close()

    val maxDistance = maxDistanceFromStart(waypoints)
    val frequentArea = mostFrequentedArea(waypoints, 0.5)  // Raggio di 0.5 km
    val geofenceCenter = Waypoint(0, 45.0, 41.0)  // Valori esempio, da custom-parameters.yml
    val geofenceRadius = 0.4  // Valore esempio
    val outsideGeofence = waypointsOutsideGeofence(waypoints, geofenceCenter, geofenceRadius)

    saveResultsToJson(maxDistance, frequentArea, outsideGeofence, geofenceCenter, geofenceRadius, 0.5)
}