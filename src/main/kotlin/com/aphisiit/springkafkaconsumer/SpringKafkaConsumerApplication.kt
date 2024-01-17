package com.aphisiit.springkafkaconsumer

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication
class SpringKafkaConsumerApplication

fun main(args: Array<String>) {
	runApplication<SpringKafkaConsumerApplication>(*args)
}
