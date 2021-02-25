package com.example.kafka

import com.example.kafka.controller.contract.CreateMessageRequest
import com.example.kafka.model.MessageModel
import com.example.kafka.repository.MessageRepository
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.databind.type.CollectionType
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNotNull
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestInstance
import org.junit.jupiter.api.extension.ExtendWith
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.web.client.TestRestTemplate
import org.springframework.boot.web.server.LocalServerPort
import org.springframework.core.ParameterizedTypeReference
import org.springframework.http.*
import org.springframework.test.context.junit.jupiter.SpringExtension


@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ExtendWith(SpringExtension::class)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class CreateMessageIntegration @Autowired constructor(
    private val messageRepository: MessageRepository,
    private val restTemplate: TestRestTemplate
) {

    @LocalServerPort
    protected var port: Int = 0

    @BeforeEach
    fun setUp() {
        messageRepository.deleteAll()
    }

    private fun getRootUrl(): String = "http://localhost:$port/messages"

    @Test
    fun `when the partition is empty, and a message is created, it should be persisted`() {

        // ACT

        val httpHeaders = HttpHeaders()
        httpHeaders.contentType = MediaType.APPLICATION_FORM_URLENCODED

        val requestBody = HashMap<String, String>()
        requestBody.set("data", "123123")

        val headers = HttpHeaders()
        headers.contentType = MediaType.APPLICATION_JSON

        val json = ObjectMapper().writeValueAsString(requestBody)
        val entity = HttpEntity(json, headers)

        restTemplate.postForObject(getRootUrl(), entity, String::class.java)

        // ASSERT

        val mapper = ObjectMapper()
        val type: CollectionType = mapper.typeFactory.constructCollectionType(
            MutableList::class.java, MessageModel::class.java
        )

        val response =  restTemplate.exchange<List<MessageModel>>(
            getRootUrl(),
            HttpMethod.GET,
            null,
            ParameterizedTypeReference.forType(type)
        )

        assertEquals(200, response.statusCodeValue)
        assertNotNull(response.body)
        assertEquals(1, response.body?.size)
        assertEquals(response.body?.get(0)?.data, "123123")
    }

    fun createMessage(): CreateMessageRequest {
        return CreateMessageRequest(
            data = "Hello"
        )
    }

}