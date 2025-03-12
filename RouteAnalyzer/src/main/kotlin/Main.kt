package it.polito.wa2.g01

import org.apache.commons.csv.CSVFormat
import org.apache.commons.csv.CSVParser
import java.io.FileReader

// Definiamo la classe Waypoint per rappresentare ogni punto del percorso
data class Waypoint(val timestamp: Long, val latitude: Double, val longitude: Double)

fun main() {
    val waypoints = mutableListOf<Waypoint>()


    val reader = FileReader("RouteAnalyzer/src/main/resources/waypoints.csv")
    val csvParser = CSVParser(reader, CSVFormat.DEFAULT.withDelimiter(';'))

    // Itera attraverso le righe del CSV e popola la lista di Waypoint
    for (csvRecord in csvParser) {
        val timestamp = csvRecord.get(0).toLongOrNull()
        val latitude = csvRecord.get(1).toDoubleOrNull()
        val longitude= csvRecord.get(2).toDoubleOrNull()

        if (timestamp != null && latitude != null && longitude != null) {
            waypoints.add(Waypoint(timestamp, latitude, longitude))
        }
    }

    csvParser.close()

    waypoints.forEach { println(it) }
}