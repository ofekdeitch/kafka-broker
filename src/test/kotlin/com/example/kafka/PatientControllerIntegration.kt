package com.example.kafka

import org.bson.types.ObjectId
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestInstance
import org.junit.jupiter.api.extension.ExtendWith
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.web.client.TestRestTemplate
import org.springframework.boot.web.server.LocalServerPort
import org.springframework.test.context.junit.jupiter.SpringExtension

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ExtendWith(SpringExtension::class)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class PatientControllerIntegration @Autowired constructor(
    private val patientRepository: PatientRepository,
    private val restTemplate: TestRestTemplate
) {

    @LocalServerPort
    protected var port: Int = 0

    @BeforeEach
    fun setUp() {
        patientRepository.deleteAll()
    }

    private fun getRootUrl(): String? = "http://localhost:$port/patients"

    private fun savePatients(name1: String, name2: String) {
        patientRepository.save(Patient(name = name1, description = "Description"))
        patientRepository.save(Patient(name = name2, description = "Description"))
    }

    @Test
    fun `should return all patients`() {
        val name1 = "Bob"
        val name2 = "Jonny"
        savePatients(name1, name2)

        val response = restTemplate.getForEntity(
            getRootUrl(),
            List::class.java
        )

        assertEquals(200, response.statusCode.value())
        assertNotNull(response.body)
        assertEquals(2, response.body?.size)
    }

}