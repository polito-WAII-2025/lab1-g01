package it.polito.wa2.g01

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory
import java.io.File

// Define a data class that matches the YAML structure
data class CustomParameters(
    val earthRadiusKm: Double = 0.0,
    val geofenceCenterLatitude: Double = 0.0,
    val geofenceCenterLongitude: Double = 0.0,
    val geofenceRadiusKm: Double = 0.0,
    val mostFrequentedAreaRadiusKm: Double = 1.5
)

// Singleton object to load YAML configuration
object ConfigLoader {
    private val objectMapper = ObjectMapper(YAMLFactory())

    fun load(filePath: String): CustomParameters {
        return objectMapper.readValue(File(filePath), CustomParameters::class.java)
    }
}