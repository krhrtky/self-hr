package com.example.applications.config

import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.context.annotation.Configuration

@ConfigurationProperties("app.aws")
class AWSConfig(
    val overrideUrl: String?,
    val region: String
)

