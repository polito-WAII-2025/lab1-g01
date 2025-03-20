package it.polito.wa2.g01

import com.uber.h3core.H3Core

object StandardAnalysis {

    fun maxDistanceFromStart(waypoints: List<Waypoint>, earthRadiusKm: Double): Pair<Waypoint, Double>? {
        if (waypoints.isEmpty()) return null
        val start = waypoints.first()
        return waypoints.maxByOrNull { start.distanceTo(it, earthRadiusKm) }
            ?.let { it to start.distanceTo(it, earthRadiusKm) }
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

    fun waypointsOutsideGeofence(
        waypoints: List<Waypoint>,
        earthRadiusKm: Double,
        geofenceCenter: Waypoint,
        geofenceRadiusKm: Double
    ): List<Waypoint> {
        return waypoints.filter { it.distanceTo(geofenceCenter, earthRadiusKm) > geofenceRadiusKm }
    }
}
