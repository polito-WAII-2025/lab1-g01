package it.polito.wa2.g01



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
    saveResultsToJson(maxDistance, frequentArea, outsideGeofence, geofenceCenter, config.geofenceRadiusKm, areaRadius = config.mostFrequentedAreaRadiusKm)

}