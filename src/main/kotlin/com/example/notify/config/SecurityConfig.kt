package com.example.notify.config

import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.security.config.web.server.ServerHttpSecurity
import org.springframework.security.oauth2.jwt.NimbusReactiveJwtDecoder
import org.springframework.security.oauth2.jwt.ReactiveJwtDecoder
import org.springframework.security.web.server.SecurityWebFilterChain
import org.springframework.web.reactive.function.client.WebClient
import javax.annotation.PostConstruct

@Configuration
class SecurityConfig(private val authServerWebClientBuilder: WebClient.Builder) {

  @Value("\${spring.security.oauth2.resourceserver.jwt.jwk-set-uri}")
  private val oauthUrl: String? = null

  private var webClient: WebClient? = null

  @PostConstruct
  fun init() {
    webClient = authServerWebClientBuilder.build()
  }

  @Bean
  protected fun springSecurityFilterChain(http: ServerHttpSecurity): SecurityWebFilterChain? {
    http.authorizeExchange().anyExchange().authenticated()
        .and().oauth2ResourceServer().jwt()
    return http.build()
  }

  @Bean
  fun jwtDecoder(): ReactiveJwtDecoder? {
    return NimbusReactiveJwtDecoder.withJwkSetUri(oauthUrl)
        .webClient(webClient)
        .build()
  }

}