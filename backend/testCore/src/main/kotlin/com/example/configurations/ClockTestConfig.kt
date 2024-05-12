package com.example.configurations

import org.springframework.boot.test.context.TestConfiguration
import org.springframework.context.annotation.Bean
import java.time.Clock
import java.time.Instant

@TestConfiguration
class ClockTestConfig {
    @Bean
    fun clock(): Clock = Clock.fixed(Instant.parse("2024-05-12T12:34:56+09:00"), ClockConfig.applicationTimeZoneId)
}
