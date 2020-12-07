package com.example.notify.util

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test

class KotlinBasicsTest {

  @Test
  fun stringSplit_WithComma() {
    val emails = "test@test.com,test1@test.com".split(",").toTypedArray()
    assertThat(emails).hasSize(2)
  }

  @Test
  fun stringSplit_Without_Comma() {
    val emails = "test@test.com".split(",").toTypedArray()
    assertThat(emails).hasSize(1)
  }

  @Test
  fun stringSplit_Without_Comma_Edge() {
    val emails = "test@test.com,".split(",").toTypedArray()
    assertThat(emails).hasSize(2)
  }

}