package com.example.core

import org.springframework.stereotype.Component
import java.time.Clock
import java.time.OffsetDateTime

@Component
class ApplicationTime(
    private val clock: Clock,
) {
    fun currentOffsetDateTime() = OffsetDateTime.now(clock)
}
