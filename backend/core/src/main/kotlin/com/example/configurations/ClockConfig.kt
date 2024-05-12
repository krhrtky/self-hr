package com.example.configurations

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import java.time.Clock
import java.time.ZoneId

@Configuration
class ClockConfig {
    @Bean
    fun clock(): Clock = Clock.system(applicationTimeZoneId)

    companion object {
        val applicationTimeZoneId = ZoneId.of("Asia/Tokyo")
    }
}
