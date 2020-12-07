package com.example.notify.config

import com.example.notify.util.SSLFactoryUtil.Companion.createSSLFactory
import lombok.SneakyThrows
import org.apache.http.client.config.RequestConfig
import org.apache.http.impl.client.HttpClientBuilder
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Value
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty
import org.springframework.cloud.config.client.ConfigClientProperties
import org.springframework.cloud.config.client.ConfigServicePropertySourceLocator
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Primary
import org.springframework.core.io.Resource
import org.springframework.http.client.ClientHttpRequestInterceptor
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory
import org.springframework.util.LinkedMultiValueMap
import org.springframework.web.client.HttpClientErrorException
import org.springframework.web.client.ResourceAccessException
import org.springframework.web.client.RestTemplate
import java.io.Serializable

@ConditionalOnProperty("spring.cloud.config.enabled", havingValue = "true")
class ConfigServiceBootstrapConfig(private val properties: ConfigClientProperties) {

  private val logger: Logger = LoggerFactory.getLogger(ConfigServiceBootstrapConfig::class.java)

  @Value("\${ssl.cloud-config.enabled ?: true}")
  private var sslCloudConfigEnabled: Boolean = true

  @Value("\${ssl.cloud-config.truststore}")
  private lateinit var sslCloudConfigTruststore: Resource

  @Value("\${ssl.cloud-config.truststore-password}")
  private lateinit var sslCloudConfigTruststorePassword: String

  @Value("\${ssl.keycloak.enabled ?: true}")
  private var sslKeycloakEnabled: Boolean = true

  @Value("\${ssl.keycloak.truststore}")
  private lateinit var sslKeycloakTruststore: Resource

  @Value("\${ssl.keycloak.truststore-password}")
  private lateinit var sslKeycloakTruststorePassword: String

  @Value("\${spring.security.oauth2.client.provider.keycloak.token-uri}")
  private lateinit var tokenEndpoint: String

  @Value("\${spring.security.oauth2.client.registration.keycloak.client-id}")
  private lateinit var clientId: String

  @Value("\${spring.security.oauth2.client.registration.keycloak.client-secret}")
  private lateinit var clientSecret: String

  @Value("\${spring.cloud.config.request-connect-timeout}")
  private val requestConnectTimeout: Int = 0


  @Primary
  @Bean
  fun configServicePropertySourceLocator(): ConfigServicePropertySourceLocator {
    val restTemplate = getRestTemplate(sslCloudConfigEnabled, sslCloudConfigTruststore,
        sslCloudConfigTruststorePassword)
    val accessToken = getAccessToken()
    val interceptor = ClientHttpRequestInterceptor { httpRequest, bytes, clientHttpRequestExecution ->
      httpRequest.headers
          .add("Authorization", "Bearer $accessToken")
      clientHttpRequestExecution.execute(httpRequest, bytes)
    }
    restTemplate.interceptors
        .add(interceptor)
    val configServicePropertySourceLocator = ConfigServicePropertySourceLocator(properties)
    configServicePropertySourceLocator.setRestTemplate(restTemplate)
    return configServicePropertySourceLocator
  }

  private fun getAccessToken(): String? {
    val restTemplate = getRestTemplate(sslKeycloakEnabled, sslKeycloakTruststore, sslKeycloakTruststorePassword)
    val form = LinkedMultiValueMap<String, Any>()
    form.add("grant_type", "client_credentials")
    form.add("scope", "openid")
    form.add("client_id", clientId)
    form.add("client_secret", clientSecret)

    var accessToken: String? = null
    try {
      val response = restTemplate.postForEntity(tokenEndpoint, form,
          AccessTokenResponse::class.java)
      if (response.body != null) {
        accessToken = response.body!!.access_token
        logger.info("AccessToken received")
      }
    } catch (exception: HttpClientErrorException) {
      logger.info("HttpClientErrorException " + exception.statusCode)
    } catch (exception1: ResourceAccessException) {
      logger.info("Connect Timeout Exception on Url - $tokenEndpoint")
    }

    return accessToken
  }

  @SneakyThrows
  private fun getRestTemplate(sslEnabled: Boolean,
                              trustStore: Resource?,
                              trustStorePassword: String?): RestTemplate {
    val requestConfig = RequestConfig.custom()
        .setConnectTimeout(requestConnectTimeout)
        .setConnectionRequestTimeout(requestConnectTimeout)
        .setSocketTimeout(requestConnectTimeout)
        .build()
    val httpClients = HttpClientBuilder.create()
        .setDefaultRequestConfig(
            requestConfig)
    if (sslEnabled) {
      val sslFactory = createSSLFactory(null, null, trustStore,
          trustStorePassword)
      val sslContext = sslFactory.sslContext
      httpClients.setSSLContext(sslContext)
          .setSSLHostnameVerifier { _, _ -> true }
    }
    val httpClient = httpClients.build()
    val requestFactory = HttpComponentsClientHttpRequestFactory(httpClient)
    return RestTemplate(requestFactory)
  }

  /**
   * Class AccessTokenResponse
   */
  private class AccessTokenResponse : Serializable {
    var access_token: String? = null
  }

}