import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
  id("org.springframework.boot") version "2.3.4.RELEASE"
  id("io.spring.dependency-management") version "1.0.10.RELEASE"
  id("com.google.cloud.tools.jib") version "2.6.0"
  id("org.sonarqube") version "2.8"
  kotlin("jvm") version "1.3.72"
  kotlin("plugin.spring") version "1.3.72"
  jacoco
}

group = "com.example"
java.sourceCompatibility = JavaVersion.VERSION_1_8

configurations {
  compileOnly {
    extendsFrom(configurations.annotationProcessor.get())
  }
}

repositories {
  mavenCentral()
}

extra["springCloudVersion"] = "Hoxton.SR8"

dependencies {
  implementation("org.springframework.boot:spring-boot-starter-amqp")
  implementation("org.springframework.boot:spring-boot-starter-webflux")
  implementation("org.springframework.boot:spring-boot-starter-actuator")
  implementation("org.springframework.boot:spring-boot-starter-mail")
  implementation("org.springframework.boot:spring-boot-starter-thymeleaf")
  implementation("org.springframework:spring-aspects")
  implementation("org.jetbrains.kotlin:kotlin-reflect")
  implementation("org.jetbrains.kotlin:kotlin-stdlib-jdk8")
  implementation("org.springframework.cloud:spring-cloud-bus")
  implementation("org.springframework.cloud:spring-cloud-starter")
  implementation("org.springframework.cloud:spring-cloud-starter-circuitbreaker-resilience4j")
  implementation("org.springframework.cloud:spring-cloud-starter-config")
  implementation("org.springframework.cloud:spring-cloud-stream-binder-rabbit")
  implementation("org.springframework.boot:spring-boot-starter-oauth2-client")
  implementation("org.springframework.boot:spring-boot-starter-oauth2-resource-server")
  implementation("org.springframework.boot:spring-boot-starter-security")
  implementation("io.github.hakky54:sslcontext-kickstart-for-netty:6.1.0")
  implementation("com.fasterxml.jackson.module:jackson-module-kotlin:2.9.8")
  implementation("org.hibernate.validator:hibernate-validator")
  implementation("net.logstash.logback:logstash-logback-encoder:6.1")
  implementation("io.jaegertracing:jaeger-client:1.4.0")
  implementation("io.opentracing.contrib:opentracing-spring-jaeger-cloud-starter:3.2.0")
  compileOnly("org.projectlombok:lombok")
  runtimeOnly("io.micrometer:micrometer-registry-prometheus")
  annotationProcessor("org.springframework.boot:spring-boot-configuration-processor")
  annotationProcessor("org.projectlombok:lombok")
  testImplementation("org.springframework.boot:spring-boot-starter-test") {
    exclude(module = "junit")
    exclude(module = "mockito-core")
  }
  testImplementation("org.junit.jupiter:junit-jupiter-api")
  testRuntimeOnly("org.junit.jupiter:junit-jupiter-engine")
  testImplementation("com.ninja-squad:springmockk:1.1.3")
  testImplementation("org.springframework.amqp:spring-rabbit-test")
  testImplementation("com.icegreen:greenmail:1.5.5")
  testImplementation("org.springframework.cloud:spring-cloud-starter-contract-stub-runner")
}

dependencyManagement {
  imports {
    mavenBom("org.springframework.cloud:spring-cloud-dependencies:${property("springCloudVersion")}")
  }
}

tasks.withType<Test> {
  useJUnitPlatform()
}

tasks.withType<KotlinCompile> {
  kotlinOptions {
    freeCompilerArgs = listOf("-Xjsr305=strict")
    jvmTarget = "1.8"
  }
}

tasks.jacocoTestReport {
  reports {
    xml.isEnabled = true
    xml.destination = file("${buildDir}/reports/jacoco/test/jacocoTestReport.xml")
  }
}

