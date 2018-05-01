package com.deliveryhero.services.crs.auth

import com.deliveryhero.services.crs.api.error.Error
import com.fasterxml.jackson.databind.SerializationFeature
import com.fasterxml.jackson.datatype.jdk8.Jdk8Module
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import org.springframework.http.MediaType
import org.springframework.security.core.AuthenticationException
import org.springframework.security.web.AuthenticationEntryPoint
import org.springframework.security.web.authentication.Http403ForbiddenEntryPoint
import org.springframework.stereotype.Component
import java.io.IOException
import java.net.MalformedURLException
import java.net.URL
import java.time.Instant
import java.util.*
import javax.servlet.ServletException
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse
import javax.ws.rs.core.HttpHeaders
import javax.ws.rs.core.Response.Status

/**
 * An alternative to Spring Security's [Http403ForbiddenEntryPoint] that returns `401` instead when e.g. no
 * authentication has been performed yet and a protected resource is accessed. It also returns a structured JSON
 * response using [Error] instead of the servlet container's HTML default.
 *
 * @author vguna
 */
// TODO: promote this and Http404ForbiddenEntryPoint to bootstrap
@Component
class Http401UnauthorizedEntryPoint : AuthenticationEntryPoint {

    companion object {

        private val objectMapper = jacksonObjectMapper()
                .registerModules(Jdk8Module(), JavaTimeModule())
                .configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false)

        /**
         * Gets the error code that will be returned in the [Error].
         *
         * @return the code - default: `authentication-error`.
         */
        private const val CODE = "authentication-error"
        /**
         * Gets the error message that will be returned in the [Error].
         *
         * @return the message - default: `Authentication is required to access this resource.`
         */
        private const val MESSAGE = "Authentication is required to access this resource."
        // Simply use the Spring Security default realm
        /**
         * Gets the realm that will be used in the [HttpHeaders.WWW_AUTHENTICATE] header when returning `401`.
         *
         * @return the realm - default: `Realm`.
         */
        private const val REALM = "Realm"
    }

    @Throws(IOException::class, ServletException::class)
    override fun commence(request: HttpServletRequest, response: HttpServletResponse, arg2: AuthenticationException) {

        // TODO use real request id
        val errorResponse = Error(Instant.now(), getRequestUrl(request).toString(), UUID.randomUUID().toString(),
                CODE, MESSAGE, setOf())

        // create JSON and set raw HTTP response attributes
        val responsePayload = objectMapper.writeValueAsString(errorResponse)
        response.setContentLength(responsePayload.length)
        response.status = Status.UNAUTHORIZED.statusCode
        response.contentType = MediaType.APPLICATION_JSON_VALUE
        response.addHeader(HttpHeaders.WWW_AUTHENTICATE,
                "${BearerAuthenticationFilter.CRS_BEARER_AUTH_SCHEME} realm=$REALM")

        // write actual error JSON response
        response.writer.write(responsePayload)
    }

    private fun getRequestUrl(request: HttpServletRequest): URL {
        try {
            val requestUrlBuffer = request.requestURL
            val queryString = request.queryString
            if (queryString != null) {
                requestUrlBuffer.append("?").append(queryString)
            }
            return URL(requestUrlBuffer.toString())
        } catch (e: MalformedURLException) {
            throw IllegalArgumentException(e)
        }

    }
}
