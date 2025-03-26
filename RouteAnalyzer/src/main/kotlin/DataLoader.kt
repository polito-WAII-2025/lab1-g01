package it.polito.wa2.g01

import org.apache.commons.csv.CSVFormat
import org.apache.commons.csv.CSVParser
import java.io.FileReader
import java.io.FileNotFoundException
import java.io.IOException

object WaypointLoader {

    fun loadWaypoints(csvFilePath: String): List<Waypoint> {
        val waypoints = mutableListOf<Waypoint>()

        try {
            val reader = FileReader(csvFilePath)
            val csvParser = CSVParser(reader, CSVFormat.DEFAULT.builder()
                .setDelimiter(';')
                .build())

            for (csvRecord in csvParser) {
                try {

                    val timestamp = csvRecord.get(0).toDoubleOrNull()?.toLong()
                    val latitude = csvRecord.get(1).toDoubleOrNull()
                    val longitude = csvRecord.get(2).toDoubleOrNull()

                    if (timestamp != null && latitude != null && longitude != null) {
                        waypoints.add(Waypoint(timestamp, latitude, longitude))
                    }
                } catch (e: NumberFormatException) {
                    println("Invalid number format in row: ${csvRecord.toString()}")
                }
            }

            csvParser.close()
        } catch (e: FileNotFoundException) {
            throw IllegalArgumentException("CSV file not found: $csvFilePath", e)
        } catch (e: IOException) {
            throw IllegalStateException("Error reading the CSV file: $csvFilePath", e)
        } catch (e: Exception) {
            throw IllegalStateException("Unexpected error occurred while loading waypoints from CSV", e)
        }

        return waypoints
    }
}
