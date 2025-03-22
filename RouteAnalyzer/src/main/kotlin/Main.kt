package it.polito.wa2.g01

import it.polito.wa2.g01.StandardAnalysis.maxDistanceFromStart
import it.polito.wa2.g01.StandardAnalysis.mostFrequentedArea
import it.polito.wa2.g01.StandardAnalysis.waypointsOutsideGeofence
import it.polito.wa2.g01.AdvancedAnalysis.calculateVelocity
import it.polito.wa2.g01.AdvancedAnalysis.detectIntersections
import kotlin.math.abs

// Function to get the H3 resolution from the most frequented area radius taken from the configuration file
fun getH3ResolutionFromRadius(mostFrequentedAreaRadiusKm: Double): Int {
    val edgeLengthByResolution = mapOf(
        0 to 1107.71,
        1 to 418.67,
        2 to 158.24,
        3 to 59.81,
        4 to 22.60,
        5 to 8.54,
        6 to 3.23,
        7 to 1.22,
        8 to 0.46,
        9 to 0.17,
        10 to 0.06
    )

    return edgeLengthByResolution.minByOrNull { (_, edgeLengthKm) ->
        abs(edgeLengthKm - mostFrequentedAreaRadiusKm)
    }?.key ?: 9 // fallback resolution if nothing matches
}


fun main() {
    // Loading configuration data
    val config = ConfigLoader.load("evaluation/custom-parameters.yml")
    // Loading waypoints from CSV file
    val waypoints = WaypointLoader.loadWaypoints("evaluation/waypoints.csv")

    // Standard analysis
    val maxDistance = maxDistanceFromStart(waypoints, config.earthRadiusKm)
    val h3Resolution = getH3ResolutionFromRadius(config.mostFrequentedAreaRadiusKm)
    val frequentArea = mostFrequentedArea(waypoints, h3Resolution)
    val geofenceCenter = Waypoint(0, config.geofenceCenterLatitude, config.geofenceCenterLongitude)
    val outsideGeofence = waypointsOutsideGeofence(waypoints, config.earthRadiusKm, geofenceCenter, config.geofenceRadiusKm)

    // Saving results to JSON file
    saveStandardOutputResultsToJson(maxDistance, frequentArea, outsideGeofence, geofenceCenter, config.geofenceRadiusKm, areaRadius = config.mostFrequentedAreaRadiusKm)


    //Advanced analysis
    val prev = waypoints[1]
    val curr = waypoints[2]
    val velocity = calculateVelocity(prev, curr, config.earthRadiusKm)
    val intersections = detectIntersections(waypoints)
    val filteredIntersections = intersections.map { it.latitude to it.longitude }

    // Saving results to JSON file
    saveOutputAdvancedResultsToJson(filteredIntersections, velocity)
}