package com.example.notify.config

import org.springframework.boot.test.context.TestConfiguration
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Primary
import org.springframework.security.config.web.server.ServerHttpSecurity
import org.springframework.security.web.server.SecurityWebFilterChain

@TestConfiguration
class SecurityConfig {

  @Primary
  @Bean
  protected fun springSecurityFilterChain(http: ServerHttpSecurity): SecurityWebFilterChain? {
    http.authorizeExchange().anyExchange().permitAll()
    return http.build()
  }

}