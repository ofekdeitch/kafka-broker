package com.example.kafka.model

import org.bson.types.ObjectId

class MessageModel (
    val id: ObjectId,
    val data: String
)