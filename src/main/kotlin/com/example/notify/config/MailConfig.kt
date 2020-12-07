package com.example.notify.config

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.mail.javamail.JavaMailSenderImpl

@Configuration
class MailConfig {

  @Bean
  fun emailSender(): JavaMailSenderImpl {
    return JavaMailSenderImpl()
  }
}