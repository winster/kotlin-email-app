package com.example.notify.listener.amqp

import com.example.notify.service.NotifyService
import org.springframework.amqp.rabbit.annotation.RabbitListener
import org.springframework.stereotype.Component

@Component
class AmqpEventListener(private val notifyService: NotifyService) {


  @RabbitListener(id = EVENT_LISTENER_ID, queues = ["\${rabbitmq.queueName}"])
  fun receiveBookingEvent(message: String) {
    notifyService.handleEvent(message)
  }

  companion object {
    const val EVENT_LISTENER_ID = "eventListener"
  }

}