package com.deliveryhero.services.crs.error

import com.deliveryhero.services.crs.api.Error
import org.springframework.http.HttpHeaders
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import org.springframework.http.ResponseEntity
import org.springframework.http.converter.HttpMessageNotReadableException
import org.springframework.web.bind.MethodArgumentNotValidException
import org.springframework.web.bind.annotation.ControllerAdvice
import org.springframework.web.bind.annotation.ExceptionHandler
import java.time.Instant
import java.util.*
import javax.servlet.http.HttpServletRequest

@ControllerAdvice
class ErrorHandler {

    /*
    when a endpoint method is called with "null" value for a non-null type parameter, e.g.
    com.deliveryhero.services.crs.api.auth.AuthController.login
     */
    @ExceptionHandler(IllegalArgumentException::class)
    fun handleIllegalArgumentException(e: IllegalArgumentException, httpServletRequest: HttpServletRequest):
            ResponseEntity<Error> =
            handleExceptionImpl(e, httpServletRequest, HttpStatus.BAD_REQUEST, "validation-error")

    @ExceptionHandler(AuthenticationException::class)
    fun handleAuthenticationException(e: AuthenticationException, httpServletRequest: HttpServletRequest):
            ResponseEntity<Error> =
            handleExceptionImpl(e, httpServletRequest, HttpStatus.UNAUTHORIZED, "auth-error")

    /*
    when a non-null type param gets "null". nested exception is then a MissingKotlinParameterException
     */
    @ExceptionHandler(HttpMessageNotReadableException::class)
    fun handleHttpMessageNotReadableException(e: HttpMessageNotReadableException, httpServletRequest: HttpServletRequest):
            ResponseEntity<Error> =
            handleExceptionImpl(e, httpServletRequest, HttpStatus.BAD_REQUEST, "validation-error")

    /*
    jsr validation issue
     */
    @ExceptionHandler(MethodArgumentNotValidException::class)
    fun handleMethodArgumentNotValidException(e: MethodArgumentNotValidException, httpServletRequest: HttpServletRequest):
            ResponseEntity<Error> =
            handleExceptionImpl(e, httpServletRequest, HttpStatus.BAD_REQUEST, "validation-error")

    private fun handleExceptionImpl(e: Exception, httpServletRequest: HttpServletRequest, httpStatus: HttpStatus,
                                    code: String): ResponseEntity<Error> {

        // TODO use real request id
        // TODO build constraint violations
        val error = Error(Instant.now(), httpServletRequest.requestURI, UUID.randomUUID().toString(),
                e.message ?: "", code, null)

        val headers = HttpHeaders().apply {
            contentType = MediaType.APPLICATION_JSON
        }

        return ResponseEntity(error, headers, httpStatus)
    }
}