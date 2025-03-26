package it.polito.wa2.g01

import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import java.nio.file.Paths

class ConfigTest {

    @Test
    fun `should load valid configuration from yaml`() {

        val filePath = Paths.get("src/test/resources/custom-parameters.yml").toString()
        val config = ConfigLoader.load(filePath)
        assertEquals(6371.0, config.earthRadiusKm)
        assertEquals(45.4642, config.geofenceCenterLatitude)
        assertEquals(9.1900, config.geofenceCenterLongitude)
        assertEquals(5.0, config.geofenceRadiusKm)
        assertEquals(1.5, config.mostFrequentedAreaRadiusKm)
    }

    @Test
    fun `should throw exception if file does not exist`() {
        val filePath = "src/test/resources/custom-param.yml"
        val exception = assertThrows<IllegalArgumentException> {
            ConfigLoader.load(filePath)
        }
        assertTrue(exception.message!!.contains("Configuration file not found"))
    }

    @Test
    fun `should throw exception on malformed yaml`() {
        val filePath = Paths.get("src/test/resources/custom-parameters-malformed.yml").toString()
        val exception = assertThrows<IllegalStateException> {
            ConfigLoader.load(filePath)
        }
        assertTrue(exception.message!!.contains("Error loading configuration"))
    }

    @Test
    fun `should throw exception on invalid values`() {
        val filePath = Paths.get("src/test/resources/custom-parameters-invalid-values.yml").toString()
        val exception = assertThrows<IllegalStateException> {
            ConfigLoader.load(filePath)
        }
        assertTrue(exception.cause is IllegalArgumentException)
        assertTrue(exception.cause!!.message!!.contains("must be greater than 0"))
    }

    @Test
    fun `should throw exception if required fields are missing in yaml`() {
        val filePath = Paths.get("src/test/resources/custom-parameters-missing-required-fields.yml").toString()

        val exception = assertThrows<IllegalStateException> {
            ConfigLoader.load(filePath)
        }
        assertTrue(exception.cause is IllegalArgumentException)
        assertTrue(exception.cause!!.message!!.contains("geofenceCenterLatitude is required and missing or zero"))
    }
}