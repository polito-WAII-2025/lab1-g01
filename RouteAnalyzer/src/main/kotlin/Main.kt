package it.polito.wa2.g01

import kotlinx.serialization.ExperimentalSerializationApi
import org.apache.commons.csv.CSVFormat
import org.apache.commons.csv.CSVParser
import kotlinx.serialization.json.Json
import kotlinx.serialization.encodeToString
import java.io.File
import java.io.FileReader
import com.uber.h3core.H3Core



fun maxDistanceFromStart(waypoints: List<Waypoint>, earthRadiusKm: Double): Pair<Waypoint, Double>? {
    if (waypoints.isEmpty()) return null

    val start = waypoints.first()
    return waypoints.maxByOrNull { start.distanceTo(it, earthRadiusKm) }
        ?.let { it to start.distanceTo(it, earthRadiusKm) }
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
fun waypointsOutsideGeofence(waypoints: List<Waypoint>, earthRadiusKm: Double, geofenceCenter: Waypoint, geofenceRadiusKm: Double): List<Waypoint> {
    return waypoints.filter { it.distanceTo(geofenceCenter, earthRadiusKm) > geofenceRadiusKm }
}

@OptIn(ExperimentalSerializationApi::class)
fun saveResultsToJson(
    maxDistance: Pair<Waypoint, Double>?,
    frequentArea: Pair<Waypoint, Int>?,
    outsideGeofence: List<Waypoint>,
    geofenceCenter: Waypoint,
    geofenceRadius: Double,
    areaRadius: Double?
) {
    val output = OutputData(
        maxDistanceFromStart = maxDistance?.let { MaxDistanceData(it.first, it.second) } ?: error("No data"),
        mostFrequentedArea = frequentArea?.let { FrequentedAreaData(it.first, areaRadius ?: 0.0, it.second) } ?: error("No data"),
        waypointsOutsideGeofence = GeoFenceData(geofenceCenter, geofenceRadius, outsideGeofence.size, outsideGeofence)
    )


    val json = Json {
        prettyPrint = true // Abilita la formattazione leggibile
        prettyPrintIndent = "  " // Indentazione di 2 spazi (puoi cambiarla)
    }

    val jsonString = json.encodeToString(output)
    File("evaluation/output.json").writeText(jsonString)
}

fun calculateVelocity(waypoint1: Waypoint, waypoint2: Waypoint, earthRadiusKm: Double){
    val segmentDistance = waypoint1.distanceTo(waypoint2, earthRadiusKm)//prev.latitude, prev.longitude, curr.latitude, curr.longitude)
    val timeDifference = (waypoint2.timestamp - waypoint1.timestamp) / (1000.0) // tempo in secondi
    println("distanza $segmentDistance")
    println(timeDifference)
    val velocita=if (timeDifference > 0) (segmentDistance/ (timeDifference/3600)) else 0.0
    println("Velocita $velocita")

}

fun mostFrequentedArea(waypoints: List<Waypoint>, resolution: Int = 9): Pair<Waypoint, Int>? {
    if (waypoints.isEmpty()) return null

    val h3 = H3Core.newInstance()
    // A map to store the frequency of each H3 cell (H3 index -> count)
    val cellFrequency = mutableMapOf<String, Int>()

    // Count the number of waypoints for each H3 cell
    for (wp in waypoints) {
        // Convert the latitude and longitude of the waypoint into an H3 index (hexagonal cell)
        val h3Index = h3.geoToH3Address(wp.latitude, wp.longitude, resolution)
        cellFrequency[h3Index] = cellFrequency.getOrDefault(h3Index, 0) + 1
    }

    // Find the H3 cell with the highest count (most frequently visited)
    val mostFrequentCell = cellFrequency.maxByOrNull { it.value } ?: return null

    // Convert the most frequent H3 index back to a geographic coordinate (center of the hexagon)
    val center = h3.h3ToGeo(mostFrequentCell.key)

    // Return a Waypoint at the center of the most visited area along with the frequency count
    return Waypoint(0, center.lat, center.lng) to mostFrequentCell.value
}


//Optional function
// Funzioni per il rilevamento delle intersezioni (simili a quelle precedentemente descritte)
data class Point(val x: Double, val y: Double)
// Calcola l'orientamento (collineare, orario, antiorario)
fun orientation(p: Point, q: Point, r: Point): Int {
    val valore = (q.y - p.y) * (r.x - q.x) - (q.x - p.x) * (r.y - q.y)
    return when {
        valore == 0.0 -> 0 // collineare
        valore > 0 -> 1 // orario
        else -> -1 // antiorario
    }
}

// Controlla se un punto è sul segmento
fun onSegment(p: Point, q: Point, r: Point): Boolean {
    return r.x in minOf(p.x, q.x)..maxOf(p.x, q.x) && r.y in minOf(p.y, q.y)..maxOf(p.y, q.y)
}

// Verifica se due segmenti si intersecano
fun doIntersect(p1: Point, p2: Point, p3: Point, p4: Point): Boolean {
    val o1 = orientation(p1, p2, p3)
    val o2 = orientation(p1, p2, p4)
    val o3 = orientation(p3, p4, p1)
    val o4 = orientation(p3, p4, p2)

    // Caso generale
    if (o1 != o2 && o3 != o4) return true

    // Casi speciali (collineari)
    if (o1 == 0 && onSegment(p1, p2, p3)) return true
    if (o2 == 0 && onSegment(p1, p2, p4)) return true
    if (o3 == 0 && onSegment(p3, p4, p1)) return true
    if (o4 == 0 && onSegment(p3, p4, p2)) return true

    return false
}

// Funzione per calcolare il punto di intersezione (approssimato)
fun calculateIntersection(p1: Point, p2: Point, p3: Point, p4: Point): Point? {
    val denom = (p1.x - p2.x) * (p3.y - p4.y) - (p1.y - p2.y) * (p3.x - p4.x)
    if (denom == 0.0) return null // Le linee sono parallele

    val x = ((p1.x * p2.y - p1.y * p2.x) * (p3.x - p4.x) - (p1.x - p2.x) * (p3.x * p4.y - p3.y * p4.x)) / denom
    val y = ((p1.x * p2.y - p1.y * p2.x) * (p3.y - p4.y) - (p1.y - p2.y) * (p3.x * p4.y - p3.y * p4.x)) / denom

    return Point(x, y)
}

// Funzione per rilevare incroci tra i percorsi
fun detectIntersections(waypoints: List<Waypoint>): List<Point> {
    val intersections = mutableListOf<Point>()

    // Convertiamo i waypoint in punti geografici (latitudine, longitudine)
    val points = waypoints.map { Point(it.longitude, it.latitude) }

    // Verifica ogni coppia di segmenti
    for (i in 0 until points.size - 1) {
        for (j in i + 2 until points.size - 1) { // Evitiamo i segmenti consecutivi
            if (doIntersect(points[i], points[i + 1], points[j], points[j + 1])) {
                // Calcolare il punto di intersezione (approssimato)
                val intersection = calculateIntersection(points[i], points[i + 1], points[j], points[j + 1])
                intersection?.let {
                    intersections.add(it)
                }
            }
        }
    }

    return intersections
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

    val maxDistance = maxDistanceFromStart(waypoints, config.earthRadiusKm)
    val frequentArea = mostFrequentedArea(waypoints)  // Raggio di 0.5 km
    val geofenceCenter = Waypoint(0, config.geofenceCenterLatitude, config.geofenceCenterLongitude)

    val outsideGeofence = waypointsOutsideGeofence(waypoints, config.earthRadiusKm, geofenceCenter, config.geofenceRadiusKm)
    println(frequentArea)
    saveResultsToJson(maxDistance, frequentArea, outsideGeofence, geofenceCenter, config.geofenceRadiusKm, areaRadius = config.mostFrequentedAreaRadiusKm)


    val intersections = detectIntersections(waypoints)
    if (intersections.isEmpty()) {
        println("Nessun incrocio trovato.")
    } else {
        println("Punti di intersezione:")
        intersections.forEach { println("Latitudine: ${it.y}, Longitudine: ${it.x}") }
    }


    //Funzione che calcola la velocità media tra 2 punti
    val prev = waypoints[1]
    val curr = waypoints[2]
    calculateVelocity(prev,curr, config.earthRadiusKm)

}