package com.example.applications.interceptors

import com.example.applications.config.AWSConfig
import jakarta.servlet.FilterChain
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.springframework.http.HttpStatus
import org.springframework.stereotype.Component
import org.springframework.web.filter.OncePerRequestFilter
import software.amazon.awssdk.regions.Region
import software.amazon.awssdk.services.cognitoidentityprovider.CognitoIdentityProviderClient
import java.net.URI

@Component
class UserInfoFilter(
    private val awsConfig: AWSConfig,
) : OncePerRequestFilter() {
    override fun doFilterInternal(
        request: HttpServletRequest,
        response: HttpServletResponse,
        filterChain: FilterChain
    ) {
        val authToken = request.getHeader("Authorization")
        val client = CognitoIdentityProviderClient.builder()
            .region(Region.of(awsConfig.region))
            .apply {
                if (awsConfig.overrideUrl != null) {
                    endpointOverride(URI(awsConfig.overrideUrl))
                }
            }
            .build()

        runCatching {
            val user = client.getUser {
                it.accessToken(authToken)
            }
            request.setAttribute("userId", user.username())
        }.onFailure {
            response.status = HttpStatus.NOT_FOUND.value()
        }
        filterChain.doFilter(request, response)
    }

    override fun shouldNotFilter(request: HttpServletRequest): Boolean {
        return pathsNotNeedsFilter.contains(request.servletPath)
    }

    companion object {
        private val pathsNotNeedsFilter = listOf(
            "/v3/api-docs",
        )
    }
}
