import it.polito.wa2.g01.AdvancedAnalysis
import it.polito.wa2.g01.Waypoint
import org.junit.jupiter.api.Test

import org.junit.jupiter.api.Assertions.*
 class AdvancedAnalysisTest {

  @Test
  fun `test calculateVelocity with valid waypoints`() {
   val waypoint1 = Waypoint(1000000000, 45.0, 7.0)
   val waypoint2 = Waypoint(1000003600, 45.001, 7.001) // 600 sec dopo
   val earthRadiusKm = 6371.0

   val velocity = AdvancedAnalysis.calculateVelocity(waypoint1, waypoint2, earthRadiusKm)

   assert(velocity > 0) { "La velocità dovrebbe essere positiva" }
  }

  @Test
  fun `test calculateVelocity with same timestamp`() {
   val waypoint1 = Waypoint(1000003600, 45.0, 7.0)
   val waypoint2 = Waypoint(1000003600, 45.001, 7.001) // Stesso timestamp
   val earthRadiusKm = 6371.0

   val velocity = AdvancedAnalysis.calculateVelocity(waypoint1, waypoint2, earthRadiusKm)

   assertEquals(0.0, velocity, "La velocità dovrebbe essere 0 quando il tempo è lo stesso")
  }

  @Test
  fun `test detectIntersections with intersecting paths`() {
   val waypoints = listOf(
    Waypoint(1, 0.0, 0.0),
    Waypoint(2, 1.0, 1.0),
    Waypoint(3, 1.0, 0.0),
    Waypoint(4, 0.0, 1.0)
   )

   val intersections = AdvancedAnalysis.detectIntersections(waypoints)

   assert(intersections.isNotEmpty()) { "Dovrebbe esserci almeno un'intersezione" }
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

   assertEquals(0, intersections.size, "Non dovrebbero esserci intersezioni")
  }
 }