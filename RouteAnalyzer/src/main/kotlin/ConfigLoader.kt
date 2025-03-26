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
){
    fun validate() {
        //requirement control
        require(earthRadiusKm != 0.0) { "earthRadiusKm is required and missing or zero" }
        require(geofenceRadiusKm != 0.0) { "geofenceRadiusKm is required and missing or zero" }
        require(geofenceCenterLatitude != 0.0) { "geofenceCenterLatitude is required and missing or zero" }
        require(geofenceCenterLongitude != 0.0) { "geofenceCenterLongitude is required and missing or zero" }

        //validation
        require(earthRadiusKm > 0) { "earthRadiusKm must be greater than 0" }
        require(geofenceRadiusKm >= 0) { "geofenceRadiusKm must be non-negative" }
        require(geofenceCenterLatitude in -90.0..90.0) { "geofenceCenterLatitude must be between -90 and 90" }
        require(geofenceCenterLongitude in -180.0..180.0) { "geofenceCenterLongitude must be between -180 and 180" }
        require(mostFrequentedAreaRadiusKm >= 0) { "mostFrequentedAreaRadiusKm must be non-negative" }
    }
}

// Singleton object to load YAML configuration
object ConfigLoader {
    private val objectMapper = ObjectMapper(YAMLFactory())

    fun load(filePath: String): CustomParameters {
        val file = File(filePath)
        if (!file.exists()) {
            throw IllegalArgumentException("Configuration file not found at: $filePath")
        }
        return try {
            val config = objectMapper.readValue(file, CustomParameters::class.java)
            config.validate()
            config
        } catch (ex: Exception) {
            throw IllegalStateException("Error loading configuration: ${ex.message}", ex)
        }
    }
}