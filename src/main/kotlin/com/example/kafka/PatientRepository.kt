package com.example.kafka

import org.bson.types.ObjectId
import org.springframework.data.mongodb.repository.MongoRepository

interface PatientRepository: MongoRepository<Patient, String> {

    fun findOneById(id: ObjectId): Patient

    override fun deleteAll()

}