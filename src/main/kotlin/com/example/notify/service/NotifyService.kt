package com.example.notify.service

import com.example.notify.config.ValidatorConfig
import com.example.notify.dto.EventMessage
import com.example.notify.util.IsoUtil.getLocale
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
import io.micrometer.core.instrument.MeterRegistry
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.mail.javamail.JavaMailSenderImpl
import org.springframework.mail.javamail.MimeMessageHelper
import org.springframework.scheduling.annotation.Async
import org.springframework.scheduling.annotation.EnableAsync
import org.springframework.stereotype.Service
import org.thymeleaf.ITemplateEngine
import org.thymeleaf.context.Context
import java.io.IOException
import java.util.*

@Service
@EnableAsync
class NotifyService(private val emailSender: JavaMailSenderImpl,
                    private val mailSenderService: MailSenderService,
                    private val validatorConfig: ValidatorConfig,
                    private val emailTemplateEngine: ITemplateEngine,
                    private val meterRegistry: MeterRegistry) {

  val logger: Logger = LoggerFactory.getLogger(NotifyService::class.java)

  @Throws(IOException::class)
  @Async
  fun handleEvent(message: String) {
    logger.debug("handleEvent {}", message)
    val eventMessage: EventMessage
    val locale: Locale
    try {
      eventMessage = jacksonObjectMapper().readValue(message)
      validatorConfig.validateFields(eventMessage)
      locale = getLocale(eventMessage.language)
    } catch (e: Exception) { //kotlin does not support multi catch. jackson may throw MissingKotlinParameterException and hibernate validator throws
      // IllegalArgument. Both ways, processing should fail. Also handling invalid language
      logger.error("Invalid message and is ignored ", e)
      meterRegistry.counter("notify.listener.invalid")
      return
    }
    meterRegistry.counter("notify.listener.received", "externalUrl", eventMessage.url)
    val ctx = Context(locale)
    ctx.setVariable("url", eventMessage.url)
    ctx.setVariable("appUrl", eventMessage.appUrl)

    val htmlContent = emailTemplateEngine.process(ALERT_EMAIL_TEMPLATE_NAME, ctx)
    logger.debug("htmlContent {}", htmlContent)
    val mimeMessage = emailSender.createMimeMessage()
    val messageHelper = MimeMessageHelper(mimeMessage, "UTF-8")
    messageHelper.setFrom(eventMessage.from)
    messageHelper.setTo(eventMessage.to.split(",").toTypedArray())
    messageHelper.setSubject(eventMessage.subject)
    messageHelper.setText(htmlContent, true)
    mailSenderService.send(mimeMessage, eventMessage.smtp)
    meterRegistry.counter("notify.listener.email.sent")
  }

  companion object {
    const val ALERT_EMAIL_TEMPLATE_NAME = "html/alert_email"
  }
}