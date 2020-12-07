package com.example.notify.service

import com.example.notify.config.ValidatorConfig
import com.example.notify.dto.EventMessage
import com.fasterxml.jackson.databind.ObjectMapper
import io.micrometer.core.instrument.MeterRegistry
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.ArgumentMatchers.eq
import org.mockito.Mockito.*
import org.mockito.junit.jupiter.MockitoExtension
import org.springframework.mail.javamail.JavaMailSenderImpl
import org.thymeleaf.ITemplateEngine
import javax.mail.internet.MimeMessage

@ExtendWith(MockitoExtension::class)
class NotifyServiceTest {

  @Test
  fun handleEvent() {
    val smtpConfig = EventMessage.SmtpConfig("smtp.net", null, null, null, false)
    val eventMessage = EventMessage("en", "user@app.com", "support@customer.com", "connection failed", "https://customer.com", "https://serer.com", smtpConfig)
    val message = ObjectMapper().writeValueAsString(eventMessage)
    val emailSender = mock(JavaMailSenderImpl::class.java)
    val mailSenderService = mock(MailSenderService::class.java)
    val validatorConfig = mock(ValidatorConfig::class.java)
    val mimeMessage = mock(MimeMessage::class.java)
    val emailTemplateEngine = mock(ITemplateEngine::class.java)
    val meterRegistry = mock(MeterRegistry::class.java)
    `when`(emailTemplateEngine.process(eq(NotifyService.ALERT_EMAIL_TEMPLATE_NAME), any())).thenReturn("<html>something</html>")
    `when`(emailSender.createMimeMessage()).thenReturn(mimeMessage)
    val notifyService = NotifyService(emailSender, mailSenderService, validatorConfig, emailTemplateEngine, meterRegistry)
    notifyService.handleEvent(message)
    verify(meterRegistry, atLeastOnce()).counter("notify.listener.email.sent")
  }
}