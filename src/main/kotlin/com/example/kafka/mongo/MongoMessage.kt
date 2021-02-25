package com.example.kafka.mongo

import org.bson.types.ObjectId
import org.springframework.data.annotation.Id
import org.springframework.data.mongodb.core.mapping.Document

@Document
data class MongoMessage(

    @Id
    val id: ObjectId = ObjectId(),

    val data: String = "",

    val offset: Long = 0

)