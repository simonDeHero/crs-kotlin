package com.deliveryhero.services.crs.auth

import org.springframework.security.authentication.AuthenticationManager
import org.springframework.security.core.Authentication
import org.springframework.security.core.AuthenticationException
import java.io.IOException
import javax.servlet.ServletException
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse

/**
 * This [AbstractCrsAuthenticationFilter] handles authentication by parsing the `Authorization` header
 * `Bearer` token from the request. The token is the ICash `SESSION9C` cookie token which is passed through
 * to ICash where needed. This filter creates a [BearerAuthenticationToken] and uses the
 * [BearerAuthenticationProvider] for the actual authentication against the ICash REST service.
 *
 * @author vguna
 */
class BearerAuthenticationFilter(
        authenticationManager: AuthenticationManager,
        unauthorizedEntryPoint: Http401UnauthorizedEntryPoint
) : AbstractCrsAuthenticationFilter(CRS_BEARER_AUTH_SCHEME, authenticationManager, unauthorizedEntryPoint) {

    @Throws(AuthenticationException::class, IOException::class, ServletException::class)
    override fun attemptAuthentication(request: HttpServletRequest, response: HttpServletResponse): Authentication {

        val sessionToken = extractAuthenticationSchemeValue(request)

        val authToken = BearerAuthenticationToken(sessionToken)

        return authenticationManager.authenticate(authToken)
    }

    companion object {
        const val CRS_BEARER_AUTH_SCHEME = "Bearer "
    }
}
