package com.example.notify.service

import com.example.notify.config.ValidatorConfig
import com.example.notify.dto.EventMessage
import com.fasterxml.jackson.databind.ObjectMapper
import io.micrometer.core.instrument.MeterRegistry
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.Mockito.*
import org.mockito.junit.jupiter.MockitoExtension
import org.springframework.mail.javamail.JavaMailSenderImpl
import org.thymeleaf.ITemplateEngine

@ExtendWith(MockitoExtension::class)
class BeanValidatorTest {

  @Test
  fun validateFields_blank() {
    //smtp.server is empty. Should be handled by validator
    val smtpConfig = EventMessage.SmtpConfig("", null, null, null, false)
    val eventMessage = EventMessage("en", "user@app.com", "support@customer.com", "connection failed", "https://test", "https://server.com", smtpConfig)
    val message = ObjectMapper().writeValueAsString(eventMessage)
    val emailSender = mock(JavaMailSenderImpl::class.java)
    val mailSenderService = mock(MailSenderService::class.java)
    val validatorConfig = ValidatorConfig()
    val emailTemplateEngine = mock(ITemplateEngine::class.java)
    val meterRegistry = mock(MeterRegistry::class.java)
    val notifyService = NotifyService(emailSender, mailSenderService, validatorConfig, emailTemplateEngine, meterRegistry)
    notifyService.handleEvent(message)
    verify(meterRegistry, atMostOnce()).counter("notify.listener.invalid")
  }

  @Test
  fun validateFields_null() {
    //language is not present, should be handled by jacksonmapper (before validator)
    val message = "{\n" +
        "  \"from\":\"user@app.com\",\n" +
        "  \"to\":\"someone@server.com\",\n" +
        "  \"subject\":\"test\",\n" +
        "  \"url\":\"https://server.com\",\n" +
        "  \"appUrl\":\"https://server.com\",\n" +
        "  \"smtp\": {\n" +
        "    \"server\":\"server.smtp.net\",\n" +
        "    \"tls\": \"true\"\n" +
        "  }\n" +
        "}"
    val emailSender = mock(JavaMailSenderImpl::class.java)
    val mailSenderService = mock(MailSenderService::class.java)
    val validatorConfig = mock(ValidatorConfig::class.java)
    val emailTemplateEngine = mock(ITemplateEngine::class.java)
    val meterRegistry = mock(MeterRegistry::class.java)
    val notifyService = NotifyService(emailSender, mailSenderService, validatorConfig, emailTemplateEngine, meterRegistry)
    notifyService.handleEvent(message)
    verify(meterRegistry, atMostOnce()).counter("notify.listener.invalid")
  }

}