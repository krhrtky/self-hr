package com.example.applications.config

import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.stereotype.Component

@Component
@ConfigurationProperties("app.aws")
class AWSConfig {
    lateinit var overrideUrl: String
    lateinit var region: String
    lateinit var userPoolId: String
    lateinit var clientPoolId: String
}
