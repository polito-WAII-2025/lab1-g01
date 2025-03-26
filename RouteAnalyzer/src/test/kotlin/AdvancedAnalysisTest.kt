import it.polito.wa2.g01.AdvancedAnalysis
import it.polito.wa2.g01.Waypoint
import org.junit.jupiter.api.Test

import org.junit.jupiter.api.Assertions.*

class AdvancedAnalysisTest {

    // Tests for the calculateVelocity function
    @Test
    fun `test calculateVelocity with valid waypoints`() {
       val waypoint1 = Waypoint(1000000000, 45.0, 7.0)
       val waypoint2 = Waypoint(1000003600, 45.001, 7.001)
       val earthRadiusKm = 6371.0

       val velocity = AdvancedAnalysis.calculateVelocity(waypoint1, waypoint2, earthRadiusKm)

       assert(velocity > 0) { "The velocity must be positive" }
    }

    // Test with identical waypoints (no movement)
    @Test
    fun `test calculateVelocity with identical waypoints`() {
        val waypoint1 = Waypoint(1000000000, 45.0, 7.0)
        val waypoint2 = Waypoint(1000003600, 45.0, 7.0) // Same position
        val earthRadiusKm = 6371.0

        val velocity = AdvancedAnalysis.calculateVelocity(waypoint1, waypoint2, earthRadiusKm)

        assertEquals(0.0, velocity, "The velocity should be 0 if the waypoints are identical")
    }

    @Test
  fun `test calculateVelocity with same timestamp`() {
   // Equal timestamps
   val waypoint1 = Waypoint(1000003600, 45.0, 7.0)
   val waypoint2 = Waypoint(1000003600, 45.001, 7.001)
   val earthRadiusKm = 6371.0

   val velocity = AdvancedAnalysis.calculateVelocity(waypoint1, waypoint2, earthRadiusKm)

   assertEquals(0.0, velocity, "The velocity should be 0 when the timestamps are equal")
  }


    // Tests for the detectIntersections function
  @Test
  fun `test detectIntersections with intersecting paths`() {
   val waypoints = listOf(
    Waypoint(1, 0.0, 0.0),
    Waypoint(2, 1.0, 1.0),
    Waypoint(3, 1.0, 0.0),
    Waypoint(4, 0.0, 1.0)
   )

   val intersections = AdvancedAnalysis.detectIntersections(waypoints)

   assert(intersections.isNotEmpty()) { "There should be at least one intersection" }
  }

  @Test
  fun `test detectIntersections with no intersections`() {
   val waypoints = listOf(
    Waypoint(1, 0.0, 0.0),
    Waypoint(2, 1.0, 0.0),
    Waypoint(3, 2.0, 0.0),
    Waypoint(4, 3.0, 0.0)
   )

   val intersections = AdvancedAnalysis.detectIntersections(waypoints)

   assertEquals(0, intersections.size, "There should be no intersections")
  }

    // Test with no waypoints
    @Test
    fun `test detectIntersections with no waypoints`() {
        val waypoints = emptyList<Waypoint>()

        val intersections = AdvancedAnalysis.detectIntersections(waypoints)

        assertEquals(0, intersections.size, "There should be no intersections with no waypoints")
    }

}