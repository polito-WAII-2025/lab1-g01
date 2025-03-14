package it.polito.wa2.g01

import kotlinx.serialization.Serializable

@Serializable
data class OutputData(
    val maxDistanceFromStart: MaxDistanceData,
    val mostFrequentedArea: FrequentedAreaData,
    val waypointsOutsideGeofence: GeoFenceData
)

@Serializable
data class MaxDistanceData(val waypoint: Waypoint, val distanceKm: Double)

@Serializable
data class FrequentedAreaData(val centralWaypoint: Waypoint, val areaRadiusKm: Double, val entriesCount: Int)

@Serializable
data class GeoFenceData(val centralWaypoint: Waypoint, val areaRadiusKm: Double, val count: Int, val waypoints: List<Waypoint>)

