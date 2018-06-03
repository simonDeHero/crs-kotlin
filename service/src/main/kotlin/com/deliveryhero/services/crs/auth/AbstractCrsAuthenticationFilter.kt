package com.deliveryhero.services.crs.auth

import com.ninecookies.common.util.Strings
import org.slf4j.LoggerFactory
import org.springframework.security.authentication.AuthenticationManager
import org.springframework.security.core.Authentication
import org.springframework.security.core.AuthenticationException
import org.springframework.security.web.authentication.AbstractAuthenticationProcessingFilter
import org.springframework.security.web.authentication.AuthenticationFailureHandler
import org.springframework.security.web.authentication.AuthenticationSuccessHandler
import org.springframework.security.web.util.matcher.AndRequestMatcher
import org.springframework.security.web.util.matcher.AntPathRequestMatcher
import org.springframework.security.web.util.matcher.RequestMatcher
import java.io.IOException
import javax.servlet.FilterChain
import javax.servlet.ServletException
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse
import javax.ws.rs.core.HttpHeaders

/**
 * This abstract [AbstractAuthenticationProcessingFilter] is used by CRS to handle the different authentication
 * schemes like `Bearer, CRS-HMAC` etc.
 *
 * @author vguna
 */
abstract class AbstractCrsAuthenticationFilter(
        authenticationScheme: String,
        authenticationManager: AuthenticationManager,
        unauthorizedEntryPoint: Http401UnauthorizedEntryPoint
) : AbstractAuthenticationProcessingFilter(createRequestMatcher(authenticationScheme)) {

    private var authenticationScheme =
            if (!authenticationScheme.endsWith(" ")) {
                "$authenticationScheme "
            } else {
                authenticationScheme
            }

    init {
        setAuthenticationManager(authenticationManager)

        // replace the default SavedRequestAwareAuthenticationSuccessHandler which we don't want (does redirect)
        setAuthenticationSuccessHandler(PopulatingMonitoringContextSuccessHandler())

        // return general 401 response on authentication errors
        setAuthenticationFailureHandler(Http401UnauthorizedFailureHandler(unauthorizedEntryPoint))
    }

    @Throws(IOException::class, ServletException::class)
    override fun successfulAuthentication(request: HttpServletRequest, response: HttpServletResponse, chain: FilterChain?,
                                          authResult: Authentication) {
        super.successfulAuthentication(request, response, chain, authResult)
        chain!!.doFilter(request, response)
    }

    /**
     * Extracts the authentication scheme value (e.g. `mytoken` of a header like
     * `Authorization: Bearer mytoken` from the given request.
     *
     * @param request the request to extract the authentication scheme value from.
     * @return the authentication scheme value.
     */
    protected fun extractAuthenticationSchemeValue(request: HttpServletRequest): String {
        return request.getHeader(HttpHeaders.AUTHORIZATION).substring(authenticationScheme.length)
    }

    /**
     * This [AuthenticationSuccessHandler] adds the current restaurantId and operatorCode to the
     * [MonitoredLoggingContext]. It can be used to track requests for specific users.
     *
     * @author vguna
     */
    private inner class PopulatingMonitoringContextSuccessHandler : AuthenticationSuccessHandler {

        @Throws(IOException::class, ServletException::class)
        override fun onAuthenticationSuccess(request: HttpServletRequest, response: HttpServletResponse,
                                             authentication: Authentication) {

            // TODO
//            val restaurantInfo = authService.getUserDetails().restaurantInfo
//            val restaurantId = restaurantInfo!!.getRestaurant().getId()
//            val operatorCode = restaurantInfo.getOperatorCode()
//            MonitoredLoggingContext.add("restaurantId", restaurantId)
//            MonitoredLoggingContext.add("operatorCode", operatorCode)
        }
    }

    /**
     * This [AuthenticationFailureHandler] takes care that a general, structured `401` JSON error response
     * is send to the client in case of an [AuthenticationException].
     *
     * @author vguna
     */
    private class Http401UnauthorizedFailureHandler(private val unauthorizedEntryPoint: Http401UnauthorizedEntryPoint)
        : AuthenticationFailureHandler {


        @Throws(IOException::class, ServletException::class)
        override fun onAuthenticationFailure(request: HttpServletRequest, response: HttpServletResponse,
                                             exception: AuthenticationException) {
            unauthorizedEntryPoint.commence(request, response, exception)
        }
    }

    /**
     * [RequestMatcher] that matches the given authentication scheme.
     *
     * @author vguna
     */
    private class AuthenticationSchemeMatcher(authenticationScheme: String) : RequestMatcher {

        private var authenticationScheme =
                if (!authenticationScheme.endsWith(" ")) {
                    "$authenticationScheme "
                } else {
                    authenticationScheme
                }

        override fun matches(request: HttpServletRequest): Boolean {

            val authHeader = request.getHeader(HttpHeaders.AUTHORIZATION)
            if (authHeader == null
                    || !authHeader.startsWith(authenticationScheme)
                    || Strings.isNullOrEmpty(authHeader.substring(authenticationScheme.length))) {
                LOG.debug("No " + authenticationScheme + "auth scheme found - skipping authentication.")
                return false
            }

            LOG.debug(authenticationScheme + "auth scheme found, performing authentication.")
            return true
        }
    }

    companion object {
        private val LOG = LoggerFactory.getLogger(AbstractCrsAuthenticationFilter::class.java)
        private fun createRequestMatcher(authenticationScheme: String): RequestMatcher {
            return AndRequestMatcher(AntPathRequestMatcher("/api/1/**"),
                    AuthenticationSchemeMatcher(authenticationScheme))
        }
    }
}
