package com.deliveryhero.services.crs.webkick

import com.deliveryhero.services.crs.CrsProperties
import com.deliveryhero.services.crs.auth.BearerAuthenticationToken
import com.deliveryhero.services.legacy.webkick.api.WebkickOperatorApi
import com.fasterxml.jackson.databind.DeserializationFeature
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.databind.SerializationFeature
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule
import com.fasterxml.jackson.jaxrs.json.JacksonJaxbJsonProvider
import org.glassfish.jersey.client.ClientConfig
import org.glassfish.jersey.client.ClientProperties
import org.glassfish.jersey.client.proxy.WebResourceFactory
import org.springframework.beans.factory.annotation.Value
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.stereotype.Service
import javax.ws.rs.client.ClientBuilder
import javax.ws.rs.client.ClientRequestFilter
import javax.ws.rs.core.Cookie
import javax.ws.rs.core.HttpHeaders

@Service
//class WebkickApiFactory(@Value("\${crs.webkickDomain}") private val webkickDomain: String) {
class WebkickApiFactory(val crsProperties: CrsProperties) {

    lateinit var operatorApi: WebkickOperatorApi

    init {
        operatorApi = createOperatorApi(createClientConfig())
    }

    private fun createOperatorApi(clientConfig: ClientConfig): WebkickOperatorApi =
            WebResourceFactory.newResource(WebkickOperatorApi::class.java,
                    ClientBuilder.newClient(clientConfig)
                            .target(crsProperties.webkickDomain)
                            .register(ClientRequestFilter {
                                // don't use UserDetailsService as this would create a cycle!
                                val authentication = SecurityContextHolder.getContext().getAuthentication()
                                if (authentication is BearerAuthenticationToken) {
                                    val sessionCookie = Cookie(WebkickOperatorApi.COOKIE_KEY, authentication.token)
                                    it.headers.add(HttpHeaders.COOKIE, sessionCookie)
                                }
                            }))

    private fun createClientConfig(): ClientConfig {

        val mapper = ObjectMapper().apply {
            disable(SerializationFeature.INDENT_OUTPUT)
            // enable Java 8 time API type serialization in Jackson object mapper
            // serializes ISO8601 date formats.
            registerModule(JavaTimeModule())
            // ISO8601 format instead of timestamps
            configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false)
            // don't use nanoseconds
            configure(SerializationFeature.WRITE_DATE_TIMESTAMPS_AS_NANOSECONDS, false)
            configure(DeserializationFeature.READ_DATE_TIMESTAMPS_AS_NANOSECONDS, false)
        }

        val jacksonProvider = JacksonJaxbJsonProvider().apply {
            setMapper(mapper)
        }

        val config = ClientConfig().apply {
            register(jacksonProvider)
        }

        return config.property(ClientProperties.FOLLOW_REDIRECTS, java.lang.Boolean.FALSE)
    }
}