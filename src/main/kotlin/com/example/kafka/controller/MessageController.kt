package com.example.kafka.controller

import com.example.kafka.Patient
import com.example.kafka.controller.contract.CreateMessageRequest
import com.example.kafka.controller.contract.UpdatePatientRequest
import com.example.kafka.model.MessageModel
import com.example.kafka.mongo.MongoMessage
import com.example.kafka.repository.MessageRepository
import org.bson.types.ObjectId
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import java.time.LocalDateTime

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

//    @GetMapping("/{id}")
//    fun getOnePatient(@PathVariable("id") id: String): ResponseEntity<Patient> {
//        val patient = messageRepository.findOneById(ObjectId(id))
//        return ResponseEntity.ok(patient)
//    }

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

//    @PutMapping("/{id}")
//    fun updatePatient(
//        @RequestBody request: UpdatePatientRequest,
//        @PathVariable("id") id: String
//    ): ResponseEntity<Patient> {
//        val patient = messageRepository.findOneById(ObjectId(id))
//        val updatedPatient = messageRepository.save(Patient(
//            id = patient.id,
//            name = request.name,
//            description =  request.description,
//            createdDate = patient.createdDate,
//            modifiedDate = LocalDateTime.now()
//        ))
//
//        return ResponseEntity.ok(updatedPatient)
//    }

}