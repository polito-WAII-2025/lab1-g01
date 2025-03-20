package it.polito.wa2.g01

object AdvancedAnalysis{
    fun calculateVelocity(wayWaypoint1: Waypoint, wayWaypoint2: Waypoint, earthRadiusKm: Double): Double {
        val segmentDistance = wayWaypoint1.distanceTo(wayWaypoint2, earthRadiusKm)//prev.latitude, prev.longitude, curr.latitude, curr.longitude)
        val timeDifference = (wayWaypoint2.timestamp - wayWaypoint1.timestamp) / (1000.0) // tempo in secondi
        val velocity=if (timeDifference > 0) (segmentDistance/ (timeDifference/3600)) else 0.0
        return velocity
    }

    //Optional function
    // Funzioni per il rilevamento delle intersezioni (simili a quelle precedentemente descritte)
    //data class Waypoint(val x: Double, val y: Double)
    // Calcola l'orientamento (collineare, orario, antiorario)
    fun orientation(p: Waypoint, q: Waypoint, r: Waypoint): Int {
        val valore = (q.longitude - p.longitude) * (r.latitude - q.latitude) - (q.latitude - p.latitude) * (r.longitude - q.longitude)
        return when {
            valore == 0.0 -> 0 // collineare
            valore > 0 -> 1 // orario
            else -> -1 // antiorario
        }
    }


    // Controlla se un punto è sul segmento
    fun onSegment(p: Waypoint, q: Waypoint, r: Waypoint): Boolean {
        return r.latitude in minOf(p.latitude, q.latitude)..maxOf(p.latitude, q.latitude) && r.longitude in minOf(p.longitude, q.longitude)..maxOf(p.longitude, q.longitude)
    }

    // Verifica se due segmenti si intersecano
    fun doIntersect(p1: Waypoint, p2: Waypoint, p3: Waypoint, p4: Waypoint): Boolean {
        val o1 = orientation(p1, p2, p3)
        val o2 = orientation(p1, p2, p4)
        val o3 = orientation(p3, p4, p1)
        val o4 = orientation(p3, p4, p2)

        // Caso generale
        if (o1 != o2 && o3 != o4) return true

        // Casi speciali (collineari)
        if (o1 == 0 && onSegment(p1, p2, p3)) return true
        if (o2 == 0 && onSegment(p1, p2, p4)) return true
        if (o3 == 0 && onSegment(p3, p4, p1)) return true
        if (o4 == 0 && onSegment(p3, p4, p2)) return true

        return false
    }

    // Funzione per calcolare il punto di intersezione (approssimato)
    fun calculateIntersection(p1: Waypoint, p2: Waypoint, p3: Waypoint, p4: Waypoint): Waypoint? {
        val denom = (p1.latitude - p2.latitude) * (p3.longitude - p4.longitude) - (p1.longitude - p2.longitude) * (p3.latitude - p4.latitude)
        if (denom == 0.0) return null // Le linee sono parallele

        val x = ((p1.latitude * p2.longitude - p1.longitude * p2.latitude) * (p3.latitude - p4.latitude) - (p1.latitude - p2.latitude) * (p3.latitude * p4.longitude - p3.longitude * p4.latitude)) / denom
        val y = ((p1.latitude * p2.longitude - p1.longitude * p2.latitude) * (p3.longitude - p4.longitude) - (p1.longitude - p2.longitude) * (p3.latitude * p4.longitude - p3.longitude * p4.latitude)) / denom

        return Waypoint(0, x, y)
    }

    // Funzione per rilevare incroci tra i percorsi
    fun detectIntersections(waypoints: List<Waypoint>): List<Waypoint> {
        val intersections = mutableListOf<Waypoint>()

        // Verifica ogni coppia di segmenti
        for (i in 0 until waypoints.size - 1) {
            for (j in i + 2 until waypoints.size - 1) { // Evitiamo i segmenti consecutivi
                if (doIntersect(waypoints[i], waypoints[i + 1], waypoints[j], waypoints[j + 1])) {
                    // Calcolare il punto di intersezione (approssimato)
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

