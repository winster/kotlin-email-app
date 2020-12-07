package com.example.notify.config

import io.jaegertracing.internal.JaegerTracer
import io.jaegertracing.internal.MDCScopeManager
import io.jaegertracing.internal.samplers.ConstSampler
import io.opentracing.Tracer
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
class TracerConfig {

  @Bean
  fun tracer(): Tracer {
    val scopeManager: MDCScopeManager = MDCScopeManager.Builder().build()
    val builder = JaegerTracer.Builder("notify")
        .withSampler(ConstSampler(true)).withScopeManager(scopeManager)
    return builder.build()
  }
}