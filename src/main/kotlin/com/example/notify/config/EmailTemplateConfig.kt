package com.example.notify.config

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.support.ResourceBundleMessageSource
import org.thymeleaf.TemplateEngine
import org.thymeleaf.spring5.SpringTemplateEngine
import org.thymeleaf.templatemode.TemplateMode
import org.thymeleaf.templateresolver.ClassLoaderTemplateResolver
import org.thymeleaf.templateresolver.ITemplateResolver
import java.util.*

@Configuration
class EmailTemplateConfig(private val templateEngine: SpringTemplateEngine) {

  @Bean
  fun emailMessageSource(): ResourceBundleMessageSource {
    val messageSource = ResourceBundleMessageSource()
    messageSource.setBasename("email/MailMessage")
    messageSource.setDefaultEncoding(EMAIL_TEMPLATE_ENCODING)
    return messageSource
  }

  @Bean
  fun emailTemplateEngine(): TemplateEngine {
    val templateEngine = SpringTemplateEngine()
    // Resolver for HTML emails (except the editable one)
    templateEngine.addTemplateResolver(htmlTemplateResolver())
    // Message source, internationalization specific to emails
    templateEngine.setTemplateEngineMessageSource(emailMessageSource())
    return templateEngine
  }

  private fun htmlTemplateResolver(): ITemplateResolver {
    val templateResolver = ClassLoaderTemplateResolver()
    templateResolver.order = Integer.valueOf(2)
    templateResolver.resolvablePatterns = Collections.singleton("html/*")
    templateResolver.prefix = "/email/"
    templateResolver.suffix = ".html"
    templateResolver.templateMode = TemplateMode.HTML
    templateResolver.characterEncoding = EMAIL_TEMPLATE_ENCODING
    templateResolver.isCacheable = false
    return templateResolver
  }

  companion object {
    const val EMAIL_TEMPLATE_ENCODING = "UTF-8"
  }
}