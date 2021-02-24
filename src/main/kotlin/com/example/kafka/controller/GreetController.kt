package com.example.kafka.controller

import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController

@RestController
class GreetController {

    @GetMapping("/greet")
    fun greet(@RequestParam("name") name: String): String {
        return "Hello, $name!"
    }
}