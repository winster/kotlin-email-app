package com.example.notify.config

import org.springframework.boot.test.context.TestConfiguration
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Primary
import org.springframework.http.client.reactive.ClientHttpConnector
import org.springframework.http.client.reactive.ReactorClientHttpConnector
import org.springframework.web.reactive.function.client.WebClient
import reactor.netty.http.client.HttpClient
import java.io.IOException
import java.security.KeyStoreException
import java.security.NoSuchAlgorithmException
import java.security.cert.CertificateException

@TestConfiguration
class WebClientConfig {

  @Bean
  @Primary
  @Throws(IOException::class, NoSuchAlgorithmException::class, KeyStoreException::class, CertificateException::class)
  fun authServerWebClientBuilder(): WebClient.Builder? {
    val httpClient = HttpClient.create()
    val httpConnector: ClientHttpConnector = ReactorClientHttpConnector(httpClient)
    return WebClient.builder().clientConnector(httpConnector)
  }
}