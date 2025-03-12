package it.polito.wa2.g01

import org.apache.commons.csv.CSVFormat
import org.apache.commons.csv.CSVParser
import java.io.FileReader

fun main() {


    val reader = FileReader("RouteAnalyzer/src/main/resources/waypoints.csv")
    val csvParser = CSVParser(reader, CSVFormat.DEFAULT.withDelimiter(';'))

    // Itera attraverso le righe del CSV
    for (csvRecord in csvParser) {
        val timeStamp = csvRecord.get(0)
        val longitudine = csvRecord.get(1)
        val latitudine = csvRecord.get(2)

        println("timeStamp: $timeStamp, Longitudine: $longitudine, Latitudine: $latitudine")
    }

    csvParser.close()
}