package it.polito.wa2.g01

import org.apache.commons.csv.CSVFormat
import org.apache.commons.csv.CSVParser
import java.io.FileReader

object WaypointLoader {
    fun loadWaypoints(csvFilePath: String): List<Waypoint> {
        val waypoints = mutableListOf<Waypoint>()
        val reader = FileReader(csvFilePath)
        val csvParser = CSVParser(reader, CSVFormat.DEFAULT.builder()
            .setDelimiter(';')
            .build())

        for (csvRecord in csvParser) {
            val timestamp = csvRecord.get(0).toDoubleOrNull()?.toLong()
            val latitude = csvRecord.get(1).toDoubleOrNull()
            val longitude = csvRecord.get(2).toDoubleOrNull()

            if (timestamp != null && latitude != null && longitude != null) {
                waypoints.add(Waypoint(timestamp, latitude, longitude))
            }
        }

        csvParser.close()
        return waypoints
    }
}
