package com.example.notify.util

import nl.altindag.ssl.SSLFactory
import org.slf4j.LoggerFactory
import org.springframework.core.io.Resource
import java.io.IOException
import java.security.KeyStoreException
import java.security.NoSuchAlgorithmException
import java.security.cert.CertificateException

class SSLFactoryUtil {

  companion object {
    private val logger = LoggerFactory.getLogger(SSLFactoryUtil::class.java)

    @Throws(IOException::class, NoSuchAlgorithmException::class, KeyStoreException::class, CertificateException::class)
    fun createSSLFactory(
        identityKeyStoreResource: Resource?,
        identityKeyStorePassword: String?,
        trustStoreResource: Resource?,
        trustStorePassword: String?): SSLFactory {

      val sslFactoryBuilder = SSLFactory.builder()
          .withDefaultTrustMaterial()
          .withProtocols("TLSv1.2")

      trustStoreResource?.let { resource ->
        trustStorePassword?.let { password ->
          sslFactoryBuilder.withTrustMaterial(resource.inputStream, password.toCharArray(), "PKCS12")
        }
      }

      identityKeyStoreResource?.let { resource ->
        identityKeyStorePassword?.let { password ->
          sslFactoryBuilder.withIdentityMaterial(resource.inputStream, password.toCharArray(), "PKCS12")
        }
      }

      val sslFactory = sslFactoryBuilder.build()

      for (cert in sslFactory.trustedCertificates) {
        logger.debug("cert DN:: ${cert.subjectDN} Issuer:: ${cert.issuerDN}")
      }

      return sslFactory
    }
  }
}