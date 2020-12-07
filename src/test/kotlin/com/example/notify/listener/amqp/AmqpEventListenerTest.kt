package com.example.notify.listener.amqp

import ch.qos.logback.classic.Logger
import com.example.notify.config.RabbitConfig
import com.example.notify.util.TestLogAppender
import com.icegreen.greenmail.util.GreenMail
import com.icegreen.greenmail.util.ServerSetup
import org.junit.jupiter.api.*
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.cloud.contract.stubrunner.StubTrigger
import org.springframework.cloud.contract.stubrunner.spring.AutoConfigureStubRunner
import org.springframework.cloud.contract.stubrunner.spring.StubRunnerProperties
import org.springframework.test.context.ContextConfiguration
import javax.mail.internet.MimeMessage


@SpringBootTest
@AutoConfigureStubRunner(ids = ["group-id:artifact-id:version:stubs"], stubsMode = StubRunnerProperties.StubsMode.REMOTE)
@ContextConfiguration(classes = [RabbitConfig::class])
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class AmqpEventListenerTest {

  private lateinit var smtpServer: GreenMail

  private lateinit var testLogAppender: TestLogAppender

  private lateinit var logger: Logger

  @BeforeAll
  @Throws(Throwable::class)
  fun before() {
    smtpServer = GreenMail(ServerSetup(2525, null, "smtp"))
    smtpServer.start()
    testLogAppender = TestLogAppender()
    logger = LoggerFactory.getLogger(Logger.ROOT_LOGGER_NAME) as Logger
    logger.addAppender(testLogAppender)
    testLogAppender.start()
  }

  val messages: Array<MimeMessage>
    get() = smtpServer.receivedMessages

  @AfterAll
  fun after() {
    smtpServer.stop()
  }

  @Autowired
  private val stubTrigger: StubTrigger? = null

  @Test
  fun externalSystemDown() {
    stubTrigger!!.trigger("label1")
    Assertions.assertTrue(smtpServer.waitForIncomingEmail(5000, 1))
    val receivedMessages: Array<MimeMessage> = messages
    Assertions.assertEquals(1, receivedMessages.size)
    testLogAppender.reset()
  }
}