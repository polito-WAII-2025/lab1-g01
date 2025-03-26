package it.polito.wa2.g01

object AdvancedAnalysis {

    //Additional function 1: CALCULATE VELOCITY

    // Function to calculate velocity between two waypoints (in km/h)
    fun calculateVelocity(wayWaypoint1: Waypoint, wayWaypoint2: Waypoint, earthRadiusKm: Double): Double {
        // Calculate the segment distance between two waypoints using the provided Earth radius
        val segmentDistance = wayWaypoint1.distanceTo(wayWaypoint2, earthRadiusKm)

        // Calculate the time difference in seconds between the two waypoints
        val timeDifference = (wayWaypoint2.timestamp - wayWaypoint1.timestamp) / (1000.0) // time in seconds

        // If time difference is positive, calculate velocity (in km/h), otherwise return 0.0 (no velocity)
        val velocity = if (timeDifference > 0) (segmentDistance / (timeDifference / 3600)) else 0.0
        return velocity
    }



    ///Additional function 2: CALCULATE INTERSECTION

    // Function to calculate orientation of three points (collinear, clockwise, counterclockwise)
    fun orientation(p: Waypoint, q: Waypoint, r: Waypoint): Int {
        // Calculate the cross product value to determine the orientation
        val value = (q.longitude - p.longitude) * (r.latitude - q.latitude) - (q.latitude - p.latitude) * (r.longitude - q.longitude)

        return when {
            value == 0.0 -> 0  // collinear
            value > 0 -> 1      // clockwise
            else -> -1          // counterclockwise
        }
    }

    // Function to check if point r is on the segment pq
    fun onSegment(p: Waypoint, q: Waypoint, r: Waypoint): Boolean {
        // Check if the point r lies on the segment pq by checking the latitude and longitude ranges
        return r.latitude in minOf(p.latitude, q.latitude)..maxOf(p.latitude, q.latitude) &&
                r.longitude in minOf(p.longitude, q.longitude)..maxOf(p.longitude, q.longitude)
    }

    // Function to check if two segments p1p2 and p3p4 intersect
    fun doIntersect(p1: Waypoint, p2: Waypoint, p3: Waypoint, p4: Waypoint): Boolean {
        // Get the orientations of the three pairs of points
        val o1 = orientation(p1, p2, p3)
        val o2 = orientation(p1, p2, p4)
        val o3 = orientation(p3, p4, p1)
        val o4 = orientation(p3, p4, p2)

        // General case: segments intersect if orientations are different
        if (o1 != o2 && o3 != o4) return true

        // Special cases (collinear points)
        if (o1 == 0 && onSegment(p1, p2, p3)) return true
        if (o2 == 0 && onSegment(p1, p2, p4)) return true
        if (o3 == 0 && onSegment(p3, p4, p1)) return true
        if (o4 == 0 && onSegment(p3, p4, p2)) return true

        return false
    }


    // Function to calculate the intersection point of two line segments (approximated)
    fun calculateIntersection(p1: Waypoint, p2: Waypoint, p3: Waypoint, p4: Waypoint): Waypoint? {
        // Calculate the denominator (to check if the lines are parallel)
        val denom = (p1.latitude - p2.latitude) * (p3.longitude - p4.longitude) - (p1.longitude - p2.longitude) * (p3.latitude - p4.latitude)

        // If denom is 0, the lines are parallel, no intersection exists
        if (denom == 0.0) return null

        // Calculate the x and y coordinates of the intersection point
        val x = ((p1.latitude * p2.longitude - p1.longitude * p2.latitude) * (p3.latitude - p4.latitude) -
                (p1.latitude - p2.latitude) * (p3.latitude * p4.longitude - p3.longitude * p4.latitude)) / denom
        val y = ((p1.latitude * p2.longitude - p1.longitude * p2.latitude) * (p3.longitude - p4.longitude) -
                (p1.longitude - p2.longitude) * (p3.latitude * p4.longitude - p3.longitude * p4.latitude)) / denom

        // Return the intersection point as a Waypoint object (with x, y coordinates)
        return Waypoint(0, x, y) // Assuming Waypoint constructor has ID as the first parameter (set to 0)
    }

    // Function to detect intersections between multiple waypoints
    fun detectIntersections(waypoints: List<Waypoint>): List<Waypoint> {
        val intersections = mutableListOf<Waypoint>()


        // Check every pair of segments for intersection
        for (i in 0 until waypoints.size - 1) {
            for (j in i + 2 until waypoints.size - 1) { // Avoid consecutive segments
                if (doIntersect(waypoints[i], waypoints[i + 1], waypoints[j], waypoints[j + 1])) {
                    // Calculate the intersection point (approximated)
                    val intersection = calculateIntersection(waypoints[i], waypoints[i + 1], waypoints[j], waypoints[j + 1])
                    intersection?.let {
                        intersections.add(it)
                    }
                }
            }
        }
        return intersections
    }
}