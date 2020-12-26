package com.example.notify.config

import com.example.notify.util.SSLFactoryUtil.Companion.createSSLFactory
import io.netty.handler.ssl.SslContext
import nl.altindag.ssl.util.NettySslUtils
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.core.io.Resource
import org.springframework.http.client.reactive.ClientHttpConnector
import org.springframework.http.client.reactive.ReactorClientHttpConnector
import org.springframework.web.reactive.function.client.WebClient
import reactor.netty.http.client.HttpClient
import reactor.netty.tcp.SslProvider.SslContextSpec

@Configuration
class WebClientConfig {

  @Value("\${ssl.keycloak.truststore}")
  private lateinit var sslKeycloakTruststore: Resource

  @Value("\${ssl.keycloak.truststore-password}")
  private lateinit var sslKeycloakTruststorePassword: String

  @Bean
  fun authServerWebClientBuilder(): WebClient.Builder? {
    val sslContext: SslContext = createSslContext(sslKeycloakTruststore, sslKeycloakTruststorePassword)
    val httpClient = HttpClient.create()
        .secure { t: SslContextSpec -> t.sslContext(sslContext) }
    val httpConnector: ClientHttpConnector = ReactorClientHttpConnector(httpClient)
    return WebClient.builder().clientConnector(httpConnector)
  }

  private fun createSslContext(trustStore: Resource,
                               trustStorePassword: String): SslContext {
    return NettySslUtils.forClient(
        createSSLFactory(
            null, null, trustStore,
            trustStorePassword))
        .build()
  }
}