package com.example.notify.service

import io.micrometer.core.instrument.MeterRegistry
import org.junit.jupiter.api.Test
import org.mockito.Mockito
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.mock.mockito.MockBean
import org.springframework.mail.javamail.JavaMailSenderImpl

@SpringBootTest
class RetryableSenderIntegrationTest {

  @Autowired
  lateinit var emailSender: JavaMailSenderImpl

  @MockBean
  lateinit var meterRegistry: MeterRegistry

  @Autowired
  lateinit var retryableSender: RetryableSender

  @Test
  fun handleEvent_retry() {
    emailSender.host = "smtp.net"
    val mimeMessage = emailSender.createMimeMessage()
    retryableSender.sendWithRetry(emailSender, mimeMessage)
    Mockito.verify(meterRegistry, Mockito.timeout(5000).atLeastOnce()).counter("notify.listener.email.failed")
  }
}