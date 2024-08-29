package com.example.testprocessor.testcontainers

import com.example.testprocessor.TestInput
import com.example.testprocessor.TestOutput
import com.example.testprocessor.TestProcessorApplication
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
import org.apache.kafka.clients.admin.AdminClientConfig
import org.apache.kafka.clients.consumer.ConsumerRecord
import org.apache.kafka.clients.producer.ProducerConfig
import org.apache.kafka.common.serialization.StringSerializer
import org.assertj.core.api.Assertions.assertThat
import org.awaitility.Awaitility
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Disabled
import org.junit.jupiter.api.Test
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.kafka.annotation.KafkaListener
import org.springframework.kafka.config.TopicBuilder
import org.springframework.kafka.core.DefaultKafkaProducerFactory
import org.springframework.kafka.core.KafkaAdmin
import org.springframework.kafka.core.KafkaTemplate
import org.springframework.kafka.core.ProducerFactory
import org.springframework.test.context.DynamicPropertyRegistry
import org.springframework.test.context.DynamicPropertySource
import org.testcontainers.containers.KafkaContainer
import org.testcontainers.utility.DockerImageName
import java.time.Duration
import java.util.concurrent.ConcurrentLinkedQueue


@SpringBootTest(
    classes = [TestProcessorApplication::class, TestKafkaConfig::class],
    webEnvironment = SpringBootTest.WebEnvironment.NONE,
    properties = [
        "com.example.testprocessor.addition=Malan",
        "spring.cloud.stream.bindings.input.destination=input-queue",
        "spring.cloud.stream.bindings.output.destination=output-queue"
    ]
)
class TestProcessorApplicationTests {
    companion object {
        val kafkaContainer = KafkaContainer(DockerImageName.parse("confluentinc/cp-kafka:latest"))
            .withEmbeddedZookeeper()

        @JvmStatic
        @BeforeAll
        fun setupAll(): Unit {
            kafkaContainer.start()
        }

        @JvmStatic
        @DynamicPropertySource
        fun configureProperties(registry: DynamicPropertyRegistry) {
            registry.add("spring.cloud.stream.kafka.binder.brokers", { kafkaContainer.bootstrapServers })
            registry.add("spring.cloud.stream.kafka.streams.binder.brokers", { kafkaContainer.bootstrapServers })
        }

        val logger = LoggerFactory.getLogger(TestProcessorApplicationTests::class.java)!!
    }

    @Autowired
    lateinit var kafkaTemplate: KafkaTemplate<String, String>

    @Autowired
    lateinit var listenerContainer: KafkaListenerContainer

    @BeforeEach
    fun setup() {
        val configs: MutableMap<String, Any> = mutableMapOf(
            Pair(AdminClientConfig.BOOTSTRAP_SERVERS_CONFIG, kafkaContainer.bootstrapServers)
        )
        val kafkaAdmin = KafkaAdmin(configs)
        TopicBuilder.name("input-queue").build()
        TopicBuilder.name("output-queue").build()
        val topics = kafkaAdmin.describeTopics("input-queue", "output-queue")
        assertThat(topics).isNotEmpty
        assertThat(topics["input-queue"]).isNotNull
        assertThat(topics["output-queue"]).isNotNull
    }

    @Test
    fun contextLoads() {

    }


    @Test
    @Disabled("fails because the topics cannot be found. Will need to investigate the best way to interact with Kafka from the tests.")
    fun testProcessorBinding() {
        // given
        val mapper = jacksonObjectMapper()
        val input = TestInput("Jacques", 1.0)
        // when
        val message = mapper.writeValueAsString(input)
        logger.info("sending:{}", message)
        kafkaTemplate.send("input-queue", message)
        // wait for data from output-queue
        Awaitility.await("message from output-queue")
            .timeout(Duration.ofSeconds(10))
            .untilAsserted {
                // when
                assertThat(listenerContainer.messages).isNotEmpty
                val outputMessage = listenerContainer.messages.poll()
                val mapper = jacksonObjectMapper()

                logger.info("received:{}", outputMessage)
                // then
                assertThat(outputMessage).isNotNull
                // when
                val output = mapper.readValue<TestOutput>(outputMessage!!.value())
                logger.info("output = {}", output)
                // then
                assertThat(output).isNull()
                assertThat(output.surname).isEqualTo("Jacques Malan")
            }
    }
}

class KafkaListenerContainer(val messages: ConcurrentLinkedQueue<ConsumerRecord<String, String>> = ConcurrentLinkedQueue<ConsumerRecord<String, String>>()) {
    companion object {
        val logger = LoggerFactory.getLogger(KafkaListenerContainer::class.java)!!
    }

    @KafkaListener(topics = ["output-queue"], groupId = "test-consumer")
    fun listener(record: ConsumerRecord<String, String>) {
        logger.info("received:{}", record)
        messages.add(record)
    }
}


@Configuration
class TestKafkaConfig {
    @Bean
    fun producerFactory(): ProducerFactory<String, String> {
        val configProps: MutableMap<String, Any> = HashMap()
        configProps[ProducerConfig.BOOTSTRAP_SERVERS_CONFIG] = TestProcessorApplicationTests.kafkaContainer.bootstrapServers
        configProps[ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG] = StringSerializer::class.java
        configProps[ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG] = StringSerializer::class.java
        return DefaultKafkaProducerFactory(configProps)
    }

    @Bean
    fun kafkaTemplate(): KafkaTemplate<String, String> {
        return KafkaTemplate(producerFactory())
    }

    @Bean
    fun kafkaListener() = KafkaListenerContainer()

}