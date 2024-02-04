package com.example.applications.interceptors

import jakarta.servlet.FilterChain
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.springframework.http.HttpStatus
import org.springframework.stereotype.Component
import org.springframework.web.filter.OncePerRequestFilter
import software.amazon.awssdk.auth.credentials.CredentialUtils
import software.amazon.awssdk.identity.spi.AwsCredentialsIdentity
import software.amazon.awssdk.regions.Region
import software.amazon.awssdk.services.cognitoidentityprovider.CognitoIdentityProviderClient
import java.net.URI

@Component
class UserInfoFilter : OncePerRequestFilter() {
    override fun doFilterInternal(
        request: HttpServletRequest,
        response: HttpServletResponse,
        filterChain: FilterChain
    ) {
        if (request.servletPath == "/v3/api-docs") {
            filterChain.doFilter(request, response)
            return
        }
        val authToken = request.getHeader("Authorization")
        val client = CognitoIdentityProviderClient.builder()
            .region(Region.AP_NORTHEAST_1)
            .credentialsProvider {
                CredentialUtils.toCredentials(AwsCredentialsIdentity.create("", ""))
            }
            .endpointOverride(URI("http://localhost:4000"))
            .build()

        runCatching {
            val user = client.getUser {
                it.accessToken(authToken)
            }
            request.setAttribute("userId", user.username())
        }.onFailure {
            response.status = HttpStatus.NOT_FOUND.value()
            response.writer.flush()
        }
        filterChain.doFilter(request, response)
    }
}
