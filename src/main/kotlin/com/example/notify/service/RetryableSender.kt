package com.example.notify.service

import io.micrometer.core.instrument.MeterRegistry
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.mail.MailSendException
import org.springframework.mail.javamail.JavaMailSender
import org.springframework.retry.annotation.Backoff
import org.springframework.retry.annotation.EnableRetry
import org.springframework.retry.annotation.Recover
import org.springframework.retry.annotation.Retryable
import org.springframework.stereotype.Component
import javax.mail.internet.MimeMessage

@Component
@EnableRetry
class RetryableSender(private val meterRegistry: MeterRegistry) {

  val logger: Logger = LoggerFactory.getLogger(NotifyService::class.java)

  @Retryable(
      value = [MailSendException::class],
      maxAttemptsExpression = "\${mail.retry.maxAttempts}",
      backoff = Backoff(delayExpression = "\${mail.retry.delay}"))
  @Throws(MailSendException::class)
  fun sendWithRetry(emailSender: JavaMailSender, mimeMessage: MimeMessage) {
    logger.trace("sendWithRetry")
    emailSender.send(mimeMessage)
  }

  @Recover
  fun recover(ex: MailSendException) {
    meterRegistry.counter("notify.listener.email.failed")
    logger.debug("mail delivery failed and cannot recover", ex)
  }
}