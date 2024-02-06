package com.aphisiit.springkafkaconsumer.configuration

import com.aphisiit.springkafkaconsumer.common.Foo2
import com.aphisiit.springkafkaconsumer.model.Student
import com.aphisiit.springkafkaconsumer.model.Message
import com.aphisiit.springkafkaconsumer.utils.log.Log
import org.apache.kafka.clients.admin.AdminClientConfig
import org.apache.kafka.clients.admin.NewTopic
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.core.task.SimpleAsyncTaskExecutor
import org.springframework.core.task.TaskExecutor
import org.springframework.kafka.annotation.KafkaListener
import org.springframework.kafka.core.KafkaAdmin
import org.springframework.kafka.core.KafkaOperations
import org.springframework.kafka.listener.DeadLetterPublishingRecoverer
import org.springframework.kafka.listener.SeekToCurrentErrorHandler
import org.springframework.kafka.support.converter.JsonMessageConverter
import org.springframework.kafka.support.converter.RecordMessageConverter
import org.springframework.util.backoff.FixedBackOff


@Configuration
class ApplicationConfig {

	companion object: Log()

	private val exec: TaskExecutor = SimpleAsyncTaskExecutor()

	@Value(value = "\${spring.kafka.bootstrap-address}")
	private lateinit var bootstrapAddress: String

	@Value(value = "\${spring.kafka.number-partitions}")
	private var numPartitions: Int = 1

	@Value(value = "\${spring.kafka.replication-factor}")
	private var replicationFactor: Int = 1

//	@Autowired
//	lateinit var template: KafkaTemplate<Any, Any>

//	@Bean
//	fun kafkaListenerContainerFactory(): KafkaOperations<Any, Any>? {
//		return template
//	}

	@Bean
	fun kafkaAdmin(): KafkaAdmin {
		val configs: MutableMap<String, Any> = HashMap()
		configs[AdminClientConfig.BOOTSTRAP_SERVERS_CONFIG] = bootstrapAddress
		return KafkaAdmin(configs)
	}

	@Bean
	fun errorHandler(template: KafkaOperations<Any?, Any?>?): SeekToCurrentErrorHandler? {
		return SeekToCurrentErrorHandler(
			DeadLetterPublishingRecoverer(template!!), FixedBackOff(1000L, 2)
		)
	}

	@Bean
	fun converter(): RecordMessageConverter? {
		return JsonMessageConverter()
	}

	@KafkaListener(id = "fooGroup", topics = ["topic1"])
	fun listen(foo: Message<Foo2>) {
		logger.info("Received: $foo")
		if (foo.message.foo!!.startsWith("fail")) {
			throw RuntimeException("failed")
		}
//		exec.execute { println("Hit Enter to terminate...") }
	}

	@KafkaListener(id = "dltGroup", topics = ["topic1.DLT"])
	fun dltListen(`in`: String) {
		logger.info("Received from DLT: $`in`")
//		exec.execute { println("Hit Enter to terminate...") }
	}

	@KafkaListener(id = "studentGroup", topics = ["student"])
	fun studentListen(student: Message<Student>) {
		logger.info("Received from student: $student")
//		exec.execute { println("Hit Enter to terminate...") }
	}

	@Bean
	fun topic(): NewTopic {
		return NewTopic("topic1", numPartitions, replicationFactor.toShort())
	}

	@Bean
	fun dlt(): NewTopic {
		return NewTopic("topic1.DLT", numPartitions, replicationFactor.toShort())
	}

	@Bean
	fun student(): NewTopic {
		return NewTopic("student", numPartitions, replicationFactor.toShort())
	}

//	@Bean
//	fun studentKafkaListenerContainerFactory(): ConcurrentKafkaListenerContainerFactory<String?, String?>? {
//		return kafkaListenerContainerFactory("studentGroup")
//	}
//
//	@Bean
//	fun fooKafkaListenerContainerFactory(): ConcurrentKafkaListenerContainerFactory<String?, String?>? {
//		return kafkaListenerContainerFactory("foo")
//	}
//
//	@Bean
//	fun barKafkaListenerContainerFactory(): ConcurrentKafkaListenerContainerFactory<String?, String?>? {
//		return kafkaListenerContainerFactory("bar")
//	}
//
//	@Bean
//	fun headersKafkaListenerContainerFactory(): ConcurrentKafkaListenerContainerFactory<String?, String?>? {
//		return kafkaListenerContainerFactory("headers")
//	}
//
//	@Bean
//	fun partitionsKafkaListenerContainerFactory(): ConcurrentKafkaListenerContainerFactory<String?, String?>? {
//		return kafkaListenerContainerFactory("partitions")
//	}
//
//	@Bean
//	fun longMessageKafkaListenerContainerFactory(): ConcurrentKafkaListenerContainerFactory<String?, String?>? {
//		return kafkaListenerContainerFactory("longMessage")
//	}
//
//	@Bean
//	fun filterKafkaListenerContainerFactory(): ConcurrentKafkaListenerContainerFactory<String?, String?> {
//		val factory = kafkaListenerContainerFactory("filter")
//		factory.setRecordFilterStrategy { record: ConsumerRecord<String?, String?> ->
//			record.value()?.contains("World") ?: false
//		}
//		return factory
//	}
//
//	fun consumerFactory(groupId: String): ConsumerFactory<Any?, Any?> {
//		val props: MutableMap<String, Any> = HashMap();
//		props[ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG] = bootstrapAddress
//		props[ConsumerConfig.GROUP_ID_CONFIG] = groupId
//		props[ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG] = StringDeserializer::class.java
//		props[ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG] = StringDeserializer::class.java
//		props[ConsumerConfig.MAX_PARTITION_FETCH_BYTES_CONFIG] = "20971520";
//		props[ConsumerConfig.FETCH_MAX_BYTES_CONFIG] = "20971520";
//		return DefaultKafkaConsumerFactory(props)
//	}
//
//
//	fun kafkaListenerContainerFactory(groupId: String): ConcurrentKafkaListenerContainerFactory<String?, String?> {
//		val factory = ConcurrentKafkaListenerContainerFactory<String?, String?>()
//		factory.consumerFactory = consumerFactory(groupId)
//		return factory
//	}
}
