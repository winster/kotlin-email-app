package com.example.notify.util

import java.util.*

object IsoUtil {
  private val ISO_LANGUAGES = Locale.getISOLanguages()

  @Throws(MissingResourceException::class)
  fun getLocale(language: String?): Locale {
    if(ISO_LANGUAGES.contains(language?.toLowerCase()))
      return Locale(language)
    else
      throw MissingResourceException("Couldn't find 2-letter language code for "
          + language, "", "ShortLanguage")
  }
}