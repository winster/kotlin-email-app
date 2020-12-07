package com.example.notify.dto

import javax.validation.Valid
import javax.validation.constraints.NotBlank
import javax.validation.constraints.NotEmpty
import javax.validation.constraints.NotNull

data class EventMessage(
    @field:NotBlank
    val language: String,
    @field:NotBlank
    val from: String,
    @field:NotBlank
    val to: String,
    @field:NotBlank
    val subject: String,
    @field:NotBlank
    val url: String,
    @field:NotBlank
    val appUrl: String,
    @field:NotNull
    @field:Valid
    val smtp: SmtpConfig) {

  data class SmtpConfig(
      @field:NotBlank
      val server: String,
      val port: Int?,
      val username: String?,
      val password: String?,
      val tls: Boolean?)
}