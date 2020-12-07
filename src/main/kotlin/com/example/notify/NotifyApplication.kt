package com.example.notify

import com.example.notify.config.props.SmtpProperties
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.context.properties.EnableConfigurationProperties
import org.springframework.boot.runApplication

@EnableConfigurationProperties(SmtpProperties::class)
@SpringBootApplication
class NotifyApplication

fun main(args: Array<String>) {
  runApplication<NotifyApplication>(*args)
}
