package com.example.notify.util

import nl.altindag.sslcontext.SSLFactory
import org.slf4j.LoggerFactory
import org.springframework.core.io.Resource
import java.io.IOException
import java.security.KeyStore
import java.security.KeyStoreException
import java.security.NoSuchAlgorithmException
import java.security.cert.CertificateException
import javax.net.ssl.TrustManagerFactory
import javax.net.ssl.X509TrustManager

class SSLFactoryUtil {

  companion object {
    private val logger = LoggerFactory.getLogger(SSLFactoryUtil::class.java)

    @Throws(IOException::class, NoSuchAlgorithmException::class, KeyStoreException::class, CertificateException::class)
    fun createSSLFactory(
        identityKeyStoreResource: Resource?,
        identityKeyStorePassword: String?, trustStoreResource: Resource?,
        trustStorePassword: String?): SSLFactory {
      val sslFactoryBuilder = SSLFactory.builder().withDefaultTrustMaterial().withProtocols("TLSv1.2")
      if (trustStoreResource != null && trustStorePassword != null) {
        val trustStore = KeyStore.getInstance("PKCS12")
        trustStore.load(trustStoreResource.inputStream,
            trustStorePassword.toCharArray())

        val tmf = TrustManagerFactory.getInstance(
            TrustManagerFactory.getDefaultAlgorithm())
        tmf.init(trustStore)
        for (tm in tmf.trustManagers) {
          if (tm is X509TrustManager) {
            for (cert in tm.acceptedIssuers) {
              logger.debug("cert DN:: {} Issuer::{}", cert.subjectDN, cert.issuerDN)
            }
          }
        }

        sslFactoryBuilder.withTrustMaterial(
            trustStore, trustStorePassword.toCharArray())
      }

      if (identityKeyStoreResource != null && identityKeyStorePassword != null) {
        val keyStore = KeyStore.getInstance("PKCS12")
        keyStore.load(identityKeyStoreResource.inputStream,
            identityKeyStorePassword.toCharArray())
        sslFactoryBuilder.withIdentityMaterial(
            keyStore, identityKeyStorePassword.toCharArray())
      }

      return sslFactoryBuilder.build()
    }
  }
}