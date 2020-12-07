package com.example.notify.service

import com.example.notify.config.ValidatorConfig
import com.example.notify.dto.EventMessage
import com.fasterxml.jackson.databind.ObjectMapper
import com.icegreen.greenmail.util.GreenMail
import com.icegreen.greenmail.util.ServerSetup
import io.micrometer.core.instrument.MeterRegistry
import org.junit.jupiter.api.AfterAll
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestInstance
import org.mockito.Mockito.timeout
import org.mockito.Mockito.verify
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.mock.mockito.MockBean
import org.springframework.mail.javamail.JavaMailSenderImpl
import org.thymeleaf.ITemplateEngine
import javax.mail.internet.MimeMessage

@SpringBootTest
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class NotifyServiceIntegrationTest {

  @Autowired
  lateinit var emailSender: JavaMailSenderImpl

  @Autowired
  lateinit var mailSenderService: MailSenderService

  @Autowired
  lateinit var validatorConfig: ValidatorConfig

  @Autowired
  lateinit var emailTemplateEngine: ITemplateEngine


  @MockBean
  lateinit var meterRegistry: MeterRegistry

  private lateinit var smtpServer: GreenMail

  @BeforeAll
  @Throws(Throwable::class)
  fun before() {
    smtpServer = GreenMail(ServerSetup(2525, null, "smtp"))
    smtpServer.start()
  }

  val messages: Array<MimeMessage>
    get() = smtpServer.receivedMessages

  @AfterAll
  fun after() {
    smtpServer.stop()
  }

  fun notifyService(): NotifyService {
    return NotifyService(emailSender = emailSender, mailSenderService = mailSenderService, validatorConfig = validatorConfig,
        emailTemplateEngine = emailTemplateEngine, meterRegistry = meterRegistry)
  }

  @Test
  fun handleEvent_Validation_failed() {
    val smtpConfig = EventMessage.SmtpConfig("smtp.net", null, null, null, false)
    val eventMessage =
        EventMessage("", "user@app.com", "support@customer.com", "connection failed", "https://customer.com", "https://server.com", smtpConfig)
    val message = ObjectMapper().writeValueAsString(eventMessage)
    notifyService().handleEvent(message)
    verify(meterRegistry, timeout(5000).atLeastOnce()).counter("notify.listener.invalid")
  }

  @Test
  fun handleEvent() {
    val smtpConfig = EventMessage.SmtpConfig("localhost", 2525, null, null, false)
    val eventMessage = EventMessage("fr", "user@app.com", "support@customer.com", "connection failed", "https://customer.com", "https://server.com", smtpConfig)
    val message = ObjectMapper().writeValueAsString(eventMessage)
    notifyService().handleEvent(message)
    //Then after that using GreenMail need to verify mail sent or not
    assertTrue(smtpServer.waitForIncomingEmail(5000, 1))
    val receivedMessages: Array<MimeMessage> = messages
    assertEquals(1, receivedMessages.size)
  }
}