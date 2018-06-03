package com.deliveryhero.services.crs.logging

import org.slf4j.LoggerFactory
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import org.springframework.web.filter.OncePerRequestFilter
import org.springframework.web.util.ContentCachingRequestWrapper
import org.springframework.web.util.ContentCachingResponseWrapper
import java.io.IOException
import java.io.UnsupportedEncodingException
import java.nio.charset.Charset
import java.util.*
import java.util.stream.Stream
import javax.servlet.FilterChain
import javax.servlet.ServletException
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse

class StubLoggingFilter : OncePerRequestFilter() {

    @Throws(ServletException::class, IOException::class)
    override fun doFilterInternal(request: HttpServletRequest, response: HttpServletResponse, filterChain: FilterChain) {
        if (isAsyncDispatch(request)) {
            filterChain.doFilter(request, response)
        } else {
            doFilterWrapped(wrapRequest(request), wrapResponse(response), filterChain)
        }
    }

    @Throws(ServletException::class, IOException::class)
    private fun doFilterWrapped(request: ContentCachingRequestWrapper, response: ContentCachingResponseWrapper,
                                filterChain: FilterChain) {
        try {
            beforeRequest(request, response)
            filterChain.doFilter(request, response)
        } finally {
            afterRequest(request, response)
            response.copyBodyToResponse()
        }
    }

    private fun beforeRequest(request: ContentCachingRequestWrapper, response: ContentCachingResponseWrapper) {
        if (LOGGER.isInfoEnabled) {
            logRequestHeader(request, request.remoteAddr + "|>")
        }
    }

    private fun afterRequest(request: ContentCachingRequestWrapper, response: ContentCachingResponseWrapper) {
        if (LOGGER.isInfoEnabled) {
            logRequestBody(request, request.remoteAddr + "|>")
            logResponse(response, request.remoteAddr + "|<")
        }
    }

    companion object {

        private val LOGGER = LoggerFactory.getLogger(StubLoggingFilter::class.java)

        private val VISIBLE_TYPES = Arrays.asList(
                MediaType.valueOf("text/*"),
                MediaType.APPLICATION_FORM_URLENCODED,
                MediaType.APPLICATION_JSON,
                MediaType.APPLICATION_XML,
                MediaType.valueOf("application/*+json"),
                MediaType.valueOf("application/*+xml"),
                MediaType.MULTIPART_FORM_DATA
        )

        private fun logRequestHeader(request: ContentCachingRequestWrapper, prefix: String) {
            val queryString = request.queryString
            if (queryString == null) {
                LOGGER.info("{} {} {}", prefix, request.method, request.requestURI)
            } else {
                LOGGER.info("{} {} {}?{}", prefix, request.method, request.requestURI, queryString)
            }
            Collections.list(request.headerNames)
                    .forEach { headerName ->
                        Collections.list(request.getHeaders(headerName))
                                .forEach { headerValue -> LOGGER.info("{} {}: {}", prefix, headerName, headerValue) }
                    }
            LOGGER.info("{}", prefix)
        }

        private fun logRequestBody(request: ContentCachingRequestWrapper, prefix: String) {
            val content = request.contentAsByteArray
            if (content.isNotEmpty()) {
                logContent(content, request.contentType, request.characterEncoding, prefix)
            }
        }

        private fun logResponse(response: ContentCachingResponseWrapper, prefix: String) {
            val status = response.status
            LOGGER.info("{} {} {}", prefix, status, HttpStatus.valueOf(status).reasonPhrase)
            response.headerNames.forEach { headerName ->
                response.getHeaders(headerName)
                        .forEach { headerValue -> LOGGER.info("{} {}: {}", prefix, headerName, headerValue) }
            }
            LOGGER.info("{}", prefix)
            val content = response.contentAsByteArray
            if (content.isNotEmpty()) {
                logContent(content, response.contentType, response.characterEncoding, prefix)
            }
        }

        private fun logContent(content: ByteArray, contentType: String, contentEncoding: String, prefix: String) {
            val mediaType = MediaType.valueOf(contentType)
            val visible = VISIBLE_TYPES.stream().anyMatch { visibleType -> visibleType.includes(mediaType) }
            if (visible) {
                try {
                    val contentString = String(content, Charset.forName(contentEncoding))
                    Stream.of(*contentString.split("\r\n|\r|\n".toRegex()).dropLastWhile { it.isEmpty() }
                            .toTypedArray()).forEach { line -> LOGGER.info("{} {}", prefix, line) }
                } catch (e: UnsupportedEncodingException) {
                    LOGGER.info("{} [{} bytes content]", prefix, content.size)
                }

            } else {
                LOGGER.info("{} [{} bytes content]", prefix, content.size)
            }
        }

        private fun wrapRequest(request: HttpServletRequest): ContentCachingRequestWrapper {
            return request as? ContentCachingRequestWrapper ?: ContentCachingRequestWrapper(request)
        }

        private fun wrapResponse(response: HttpServletResponse): ContentCachingResponseWrapper {
            return response as? ContentCachingResponseWrapper ?: ContentCachingResponseWrapper(response)
        }
    }
}
