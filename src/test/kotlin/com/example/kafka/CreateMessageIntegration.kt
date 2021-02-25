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
import org.awaitility.kotlin.await
import org.awaitility.kotlin.matches
import org.awaitility.kotlin.untilCallTo
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

        val body = CreateMessageRequest("foo")
        sendCreateMessageRequest(body)

        // ASSERT

        val response = sendGetAllMessagesRequest()

        assertEquals(200, response.statusCodeValue)
        assertNotNull(response.body)
        assertEquals(1, response.body?.size)
        assertEquals(response.body?.get(0)?.data, "foo")
    }

    @Test
    fun `when 50 messages are created, the offsets should make a range of 0 to 50`() {
        val count = 50;

        // ACT
        for (i in 0 until count) {
            val thread = Thread {
                val body = CreateMessageRequest("foo")
                sendCreateMessageRequest(body)
            }
            thread.start()
        }

        // ASSERT
        await.untilCallTo { messageRepository.count() } matches { currentCount -> currentCount == count.toLong() }

        val response = sendGetAllMessagesRequest()
        assertEquals(200, response.statusCodeValue)
        assertNotNull(response.body)
        assertEquals(count, response.body?.size)

        val offsets = response.body?.map{it.offset}!!.sorted()

        for (i in 0 until count) {
            assertEquals(i.toLong(), offsets[i])
        }
    }

    private fun sendCreateMessageRequest(body: CreateMessageRequest) {
        val request: HttpEntity<CreateMessageRequest> = HttpEntity<CreateMessageRequest>(
            body
        )

        restTemplate.exchange(
            getRootUrl(),
            HttpMethod.POST,
            request,
            CreateMessageRequest::class.java
        )
    }

    private fun sendGetAllMessagesRequest(): ResponseEntity<List<MessageModel>> {
        val mapper = ObjectMapper()
        val type: CollectionType = mapper.typeFactory.constructCollectionType(
            MutableList::class.java, MessageModel::class.java
        )

        return restTemplate.exchange<List<MessageModel>>(
            getRootUrl(),
            HttpMethod.GET,
            null,
            ParameterizedTypeReference.forType(type)
        )
    }

}