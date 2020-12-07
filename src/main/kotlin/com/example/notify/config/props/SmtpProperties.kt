package com.example.notify.config.props

import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.boot.context.properties.ConstructorBinding

@ConstructorBinding
@ConfigurationProperties(prefix = "mail")
data class SmtpProperties(
    val debug: Boolean?,
    val proxyHost: String?,
    val proxyPort: Int?,
    val proxyUser: String?,
    val proxyPassword: String?

)