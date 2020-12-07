package com.example.notify.config

import org.springframework.context.annotation.Configuration
import java.util.stream.Collectors
import javax.validation.ConstraintViolation
import javax.validation.Validation
import javax.validation.ValidatorFactory

@Configuration
class ValidatorConfig(private var validatorFactory: ValidatorFactory = Validation.buildDefaultValidatorFactory()) {

  @Throws(IllegalArgumentException::class)
  fun <T> validateFields(obj: T) {
    val validator = validatorFactory.validator
    val failedValidations = validator.validate(obj)
    if (!failedValidations.isEmpty()) {
      val allErrors = failedValidations.stream()
          .map { failure: ConstraintViolation<T> ->
            (failure.propertyPath
                .toString() + " : "
                + failure.message)
          }
          .collect(
              Collectors.toList())
      throw IllegalArgumentException(allErrors.stream().reduce { acc, err -> acc + err }.orElse(""))
    }
  }

}