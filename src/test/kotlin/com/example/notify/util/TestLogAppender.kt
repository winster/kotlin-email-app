package com.example.notify.util

import ch.qos.logback.classic.Level
import ch.qos.logback.classic.spi.ILoggingEvent
import ch.qos.logback.core.read.ListAppender
import java.util.stream.Collectors

class TestLogAppender : ListAppender<ILoggingEvent>() {

  fun reset() {
    list.clear()
  }

  fun contains(string: String?, level: Level): Boolean {
    return list.stream()
        .anyMatch { event: ILoggingEvent ->
          (event.message.toString().contains(string!!)
              && event.level == level)
        }
  }

  fun search(string: String?): List<ILoggingEvent?>? {
    return list.stream()
        .filter { event: ILoggingEvent -> event.message.toString().contains(string!!) }
        .collect(Collectors.toList())
  }

  fun countEventsForPattern(string: String?): Int {
    return list.stream()
        .filter { event: ILoggingEvent -> event.message.toString().contains(string!!) }
        .collect(Collectors.toList())
        .size
  }

}