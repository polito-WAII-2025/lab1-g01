package it.polito.wa2.g01

import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.json.Json
import java.io.File

// Standard output data
@OptIn(ExperimentalSerializationApi::class)
fun saveStandardOutputResultsToJson(
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
    val json = Json { prettyPrint = true; prettyPrintIndent = "  " }
    val jsonString = json.encodeToString(OutputData.serializer(), output)

    /* Usare il percorso del container per fare riferimento a dove si vuole scrivere il file.
       Docker si occupa di sincronizzare quella cartella con il sistema host
    */
    File("/app/inputFiles/output.json").writeText(jsonString)
    println("File output.json saved successfully")
}


// Advanced output data
@OptIn(ExperimentalSerializationApi::class)
fun saveOutputAdvancedResultsToJson(
    intersections: List<Pair<Double, Double>>,
    velocity: Double
) {

    val outputAdvanced = OutputAdvancedData(intersections, velocity)

    val json = Json { prettyPrint = true; prettyPrintIndent = "  " }
    val jsonString = json.encodeToString(OutputAdvancedData.serializer(), outputAdvanced)

    /* Usare il percorso del container per fare riferimento a dove si vuole scrivere il file.
       Docker si occupa di sincronizzare quella cartella con il sistema host
    */
    File("/app/inputFiles/output_advanced.json").writeText(jsonString)
    println("File output_advanced.json saved successfully")
}
