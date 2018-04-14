package com.deliveryhero.services.crs.auth

import org.springframework.stereotype.Component
import org.springframework.web.filter.GenericFilterBean
import javax.servlet.FilterChain
import javax.servlet.ServletRequest
import javax.servlet.ServletResponse
import javax.servlet.http.HttpServletRequest

@Component
class TokenFilter : GenericFilterBean() {

    override fun doFilter(servletRequest: ServletRequest?, servletResponse: ServletResponse?,
                          filterChain: FilterChain?) {

        val httpServletRequest = servletRequest as HttpServletRequest

        if (httpServletRequest.contextPath.contains("auth")) {
            filterChain!!.doFilter(servletRequest, servletResponse)
            return
        }

        val token = httpServletRequest.getHeader("Authorization")?.replace("Bearer ", "", true)

        try {
            RequestToken.set(token)
            filterChain!!.doFilter(servletRequest, servletResponse)
        } finally {
            RequestToken.clean()
        }
    }
}