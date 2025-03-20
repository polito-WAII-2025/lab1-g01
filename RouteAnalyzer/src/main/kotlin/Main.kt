package it.polito.wa2.g01

import it.polito.wa2.g01.AdvancedAnalysis.calculateVelocity
import it.polito.wa2.g01.AdvancedAnalysis.detectIntersections


fun main() {
    // Loading configuration data
    val config = ConfigLoader.load("evaluation/custom-parameters.yml")

    // Loading waypoints from CSV file
    val waypoints = WaypointLoader.loadWaypoints("evaluation/waypoints.csv")

    // Standard analysis
    val maxDistance = StandardAnalysis.maxDistanceFromStart(waypoints, config.earthRadiusKm)
    val frequentArea = StandardAnalysis.mostFrequentedArea(waypoints)
    val geofenceCenter = Waypoint(0, config.geofenceCenterLatitude, config.geofenceCenterLongitude)
    val outsideGeofence = StandardAnalysis.waypointsOutsideGeofence(waypoints, config.earthRadiusKm, geofenceCenter, config.geofenceRadiusKm)

    // Saving results to JSON file
    saveStandardOutputResultsToJson(maxDistance, frequentArea, outsideGeofence, geofenceCenter, config.geofenceRadiusKm, areaRadius = config.mostFrequentedAreaRadiusKm)

    val intersections = detectIntersections(waypoints)


    //Funzione che calcola la velocità media tra 2 punti
    val prev = waypoints[1]
    val curr = waypoints[2]
    val velocity = calculateVelocity(prev,curr, config.earthRadiusKm)

    val filteredIntersections = intersections.map { Pair(it.latitude, it.longitude) }
    saveOutputAdvancedResultsToJson(filteredIntersections, velocity)
}