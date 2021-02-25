package com.example.kafka.repository

import com.example.kafka.mongo.MongoMessage
import org.bson.types.ObjectId
import org.springframework.data.mongodb.repository.MongoRepository

interface MessageRepository : MongoRepository<MongoMessage, String> {

    fun findOneById(id: ObjectId): MongoMessage

    override fun deleteAll()

}