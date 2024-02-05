package com.example.applications.config

import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.lang.Nullable
import org.springframework.stereotype.Component

@Component
@ConfigurationProperties("app.aws")
class AWSConfig {
    @Nullable
    var overrideUrl: String? = null
    lateinit var region: String

}
