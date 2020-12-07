package com.example.notify.service

import com.example.notify.config.props.SmtpProperties
import com.example.notify.dto.EventMessage
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.mail.javamail.JavaMailSenderImpl
import org.springframework.stereotype.Service
import java.util.*
import javax.mail.internet.MimeMessage

@Service
class MailSenderService(private val emailSender: JavaMailSenderImpl,
                        private val retryableSender: RetryableSender,
                        private val smtpProperties: SmtpProperties) {

  val logger: Logger = LoggerFactory.getLogger(NotifyService::class.java)

  fun send(mimeMessage: MimeMessage, smtpConfig: EventMessage.SmtpConfig) {
    logger.debug("send :: entry {} {}", mimeMessage, smtpConfig)
    val props: Properties = emailSender.javaMailProperties
    emailSender.host = smtpConfig.server
    val port = smtpConfig.port
    if (port != null)
      emailSender.port = port.toInt()
    if (smtpConfig.username != null && smtpConfig.password != null) {
      emailSender.username = smtpConfig.username
      emailSender.password = smtpConfig.password
      props["mail.smtp.auth"] = "true"
    }
    smtpConfig.tls.let { props["mail.smtp.starttls.enable"] = it }
    smtpProperties.debug?.let { props["mail.debug"] = it }
    props["mail.transport.protocol"] = "smtp"
    smtpProperties.proxyHost?.let { host ->
      logger.debug("smtpProperties.proxyHost {}", host)
      logger.debug("smtpProperties.proxyHost {}", smtpProperties.proxyHost)
      props["mail.smtp.proxy.host"] = host
      smtpProperties.proxyPort?.let { props["mail.smtp.proxy.port"] = it }
      smtpProperties.proxyUser?.let { props["mail.smtp.proxy.user"] = it }
      smtpProperties.proxyPassword?.let { props["mail.smtp.proxy.password"] = it }
    }
    retryableSender.sendWithRetry(emailSender, mimeMessage)
  }

}