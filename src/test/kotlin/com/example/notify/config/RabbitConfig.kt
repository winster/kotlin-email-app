package com.example.notify.config

import org.springframework.amqp.core.*
import org.springframework.beans.factory.annotation.Value
import org.springframework.boot.test.context.TestConfiguration
import org.springframework.context.annotation.Bean

/**
 * This class defines exchange, queue and binding so that contract-test-stub can map exchange which is defined in the contract to a listener
 * Whereas it is not required in the main folder as the creation of rabbitmq resources(exchange, queue and binding) are the responsibility of
 * a publisher
 */
@TestConfiguration
class RabbitConfig {

  @Value("\${rabbitmq.exchangeName}")
  private val exchangeName: String? = null

  @Value("\${rabbitmq.queueName}")
  private val queueName: String? = null

  @Value("\${rabbitmq.routingKey}")
  private val routingKey: String? = null

  @Bean
  fun eventsExchange(): DirectExchange? {
    return ExchangeBuilder.directExchange(exchangeName)
        .ignoreDeclarationExceptions()
        .build()
  }

  @Bean
  fun eventsQueue(): Queue? {
    return QueueBuilder.durable(queueName)
        .build()
  }

  @Bean
  fun eventsBinding(): Binding? {
    return BindingBuilder.bind(eventsQueue())
        .to(eventsExchange())
        .with(routingKey)
  }


}