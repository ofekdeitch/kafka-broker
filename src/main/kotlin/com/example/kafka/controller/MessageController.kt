package com.example.kafka.controller

import com.example.kafka.controller.contract.CreateMessageRequest
import com.example.kafka.model.MessageModel
import com.example.kafka.mongo.MongoMessage
import com.example.kafka.repository.MessageRepository
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/messages")
class MessageController(
    private val messageRepository: MessageRepository
) {

    @GetMapping
    fun getAllMessages(): ResponseEntity<List<MessageModel>> {
        val messages = messageRepository.findAll().map {
            MessageModel(
                id = it.id,
                data = it.data
            )
        }

        return ResponseEntity.ok(messages)
    }

    @PostMapping
    fun createMessage(@RequestBody request: CreateMessageRequest): ResponseEntity<MessageModel> {
        val mongoMessage = messageRepository.save(
            MongoMessage(
                data = request.data
            )
        )

        return ResponseEntity(
            MessageModel(
                id = mongoMessage.id,
                data = mongoMessage.data
            ), HttpStatus.CREATED
        )
    }

}