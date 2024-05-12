package com.example.applications.interceptors

import com.example.applications.libs.Authenticator
import jakarta.servlet.FilterChain
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.springframework.http.HttpStatus
import org.springframework.stereotype.Component
import org.springframework.web.filter.OncePerRequestFilter

@Component
class UserInfoFilter(
    private val authenticator: Authenticator,
) : OncePerRequestFilter() {
    override fun doFilterInternal(
        request: HttpServletRequest,
        response: HttpServletResponse,
        filterChain: FilterChain
    ) {
        request.getHeader("Authorization")
            ?.let(authenticator::getUserInfoFrom)
            ?.let {
                request.setAttribute("userId", it.id)
            }
            ?: run {
                response.sendError(HttpStatus.UNAUTHORIZED.value())
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
