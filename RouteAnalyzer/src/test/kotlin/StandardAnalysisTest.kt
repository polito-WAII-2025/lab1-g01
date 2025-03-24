import it.polito.wa2.g01.StandardAnalysis
import it.polito.wa2.g01.Waypoint
import org.junit.jupiter.api.Test

import org.junit.jupiter.api.Assertions.*

class StandardAnalysisTest {

 // Test the maxDistanceFromStart function
 @Test
  fun maxDistanceFromStart() {
  val start = Waypoint(0, 45.0, 7.0)
  val point2 = Waypoint(1, 46.0, 8.0)
  val point3 = Waypoint(2, 47.0, 9.0)
  val waypoints = listOf(start, point2, point3)
  val (farthestPoint, distance) = StandardAnalysis.maxDistanceFromStart(waypoints, 6371.0)!!

  assertEquals(point3, farthestPoint)
  assertEquals(270.0, distance, 1.0)
  }

 // Test for maxDistanceFromStart with empty list
 @Test
 fun maxDistanceFromStart_emptyList() {
  val waypoints = emptyList<Waypoint>()
  val result = StandardAnalysis.maxDistanceFromStart(waypoints, 6371.0)
  assertNull(result)
 }

 // Test for maxDistanceFromStart with a single waypoint
 @Test
 fun maxDistanceFromStart_singleWaypoint() {
  val start = Waypoint(0, 45.0, 7.0)
  val waypoints = listOf(start)
  val (farthestPoint, distance) = StandardAnalysis.maxDistanceFromStart(waypoints, 6371.0)!!
  assertEquals(start, farthestPoint)
  assertEquals(0.0, distance, 0.0)
 }

 // Test the mostFrequentedArea function
 @Test
  fun mostFrequentedArea() {
  // Create a list of example waypoints
  val waypoints = listOf(
   Waypoint(0, 45.0, 7.0),  // Point 1
   Waypoint(1, 45.0, 7.0),  // Point 1 (repeated)
   Waypoint(2, 46.0, 8.0),  // Point 2
   Waypoint(3, 45.0, 7.0),  // Point 1 (repeated)
   Waypoint(4, 47.0, 9.0),  // Point 3
   Waypoint(5, 45.0, 7.0),  // Point 1 (repeated)
   Waypoint(6, 46.0, 8.0)   // Point 2 (repeated)
  )

  // Call the mostFrequentedArea function
  val result = StandardAnalysis.mostFrequentedArea(waypoints)

  // Verify that the most frequented point is (45.0, 7.0) with a frequency of 4
  val expectedWaypoint = Waypoint(0, 45.0, 7.0)  // The most visited point
  val expectedFrequency = 4  // The point (45.0, 7.0) appears 4 times

  // Verify that the result is correct
  assertEquals(expectedWaypoint.latitude, result!!.first.latitude, 1.0)
  assertEquals(expectedWaypoint.longitude, result.first.longitude, 1.0)
  assertEquals(expectedFrequency, result.second)
  }

 // Test for mostFrequentedArea with an empty list
 @Test
 fun mostFrequentedArea_emptyList() {
  val waypoints = emptyList<Waypoint>()
  val result = StandardAnalysis.mostFrequentedArea(waypoints)
  assertNull(result)
 }

 // Test the waypointsOutsideGeofence function
 @Test
  fun waypointsOutsideGeofence() {
  val center = Waypoint(0, 45.0, 7.0)
  val insidePoint = Waypoint(1, 45.001, 7.001)
  val outsidePoint = Waypoint(2, 46.0, 8.0)
  val waypoints = listOf(insidePoint, outsidePoint)

  val outside = StandardAnalysis.waypointsOutsideGeofence(waypoints, 6371.0, center, 5.0)
  assertEquals(1, outside.size)
  assertEquals(outsidePoint, outside.first())

  }

 // Test for waypointsOutsideGeofence with no points outside the geofence
 @Test
 fun waypointsOutsideGeofence_noPointsOutside() {
  val center = Waypoint(0, 45.0, 7.0)
  val insidePoint = Waypoint(1, 45.001, 7.001)
  val waypoints = listOf(insidePoint)

  val outside = StandardAnalysis.waypointsOutsideGeofence(waypoints, 6371.0, center, 5.0)
  assertTrue(outside.isEmpty())
 }

 // Test for waypointsOutsideGeofence with all points outside
 @Test
 fun waypointsOutsideGeofence_allPointsOutside() {
  val center = Waypoint(0, 45.0, 7.0)
  val outsidePoint1 = Waypoint(1, 46.0, 8.0)
  val outsidePoint2 = Waypoint(2, 47.0, 9.0)
  val waypoints = listOf(outsidePoint1, outsidePoint2)

  val outside = StandardAnalysis.waypointsOutsideGeofence(waypoints, 6371.0, center, 5.0)
  assertEquals(2, outside.size)
  assertTrue(outside.containsAll(waypoints))
 }

 // Test for waypointsOutsideGeofence with identical points
 @Test
 fun waypointsOutsideGeofence_withDuplicateWaypoints() {
  val center = Waypoint(0, 45.0, 7.0)
  val outsidePoint = Waypoint(1, 46.0, 8.0)
  val waypoints = listOf(outsidePoint, outsidePoint)

  val outside = StandardAnalysis.waypointsOutsideGeofence(waypoints, 6371.0, center, 5.0)
  assertEquals(1, outside.size)
  assertEquals(outsidePoint, outside.first())
 }

}