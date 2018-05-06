package com.deliveryhero.services.crs.error

import com.deliveryhero.services.crs.api.error.ConstraintViolation
import com.deliveryhero.services.crs.api.error.Error
import com.fasterxml.jackson.module.kotlin.MissingKotlinParameterException
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

    companion object {
        /*
        Regex.fromLiteral() does not behave as expected!
        see https://stackoverflow.com/questions/45035672/what-exactly-does-a-regex-created-with-regex-fromliteral-match
         */
        private val NOT_NULL_CREATOR_PATTERN =
                ".*due to missing \\(therefore NULL\\) value for creator parameter (.+) which is a non-nullable type.*"
                        .toRegex()
        private val NOT_NULL_PARAMETER_PATTERN =
                "Parameter specified as non-null is null: method ([a-zA-Z0-9.]+), parameter ([a-zA-Z0-9.]+)"
                        .toRegex()
        private val NOT_NULL_ASSERTION_PATTERN =
                "the parameter '(.+)' must not be null or empty".toRegex()

        private const val MODEL_VALIDATION_FAILED = "model-validation-error"
        private const val AUTHENTICATION_ERROR = "authentication-error"
    }

    // TODO do same non-nullhandling as below
    /*
    when a endpoint method is called with "null" value for a non-null type parameter, e.g.
    com.deliveryhero.services.crs.api.auth.AuthController.login
     */
    @ExceptionHandler(IllegalArgumentException::class)
    fun handleIllegalArgumentException(e: IllegalArgumentException, httpServletRequest: HttpServletRequest):
            ResponseEntity<Error> {

        var message = e.message ?: "invalid parameters"
        val constraintViolations = mutableSetOf<ConstraintViolation>()

        // TODO make if-cascade nicer
        var matchResult = NOT_NULL_PARAMETER_PATTERN.matchEntire(message)
        if (matchResult != null) {
            val fieldName = matchResult.groups[2]!!.value
            message = "Model validation failed."
            constraintViolations.add(ConstraintViolation(fieldName, "not-empty", "null",
                    "'$fieldName' may not be empty."))
        }
        if (matchResult == null) {
            matchResult = NOT_NULL_ASSERTION_PATTERN.matchEntire(message)
            if (matchResult != null) {
                val fieldName = matchResult.groups[1]!!.value
                message = "Model validation failed."
                constraintViolations.add(ConstraintViolation(fieldName, "not-empty", "",
                        "'$fieldName' may not be empty."))
            }
        }

        return handleExceptionImpl(message, httpServletRequest, HttpStatus.BAD_REQUEST, MODEL_VALIDATION_FAILED,
                constraintViolations)
    }

    @ExceptionHandler(AuthenticationException::class)
    fun handleAuthenticationException(e: AuthenticationException, httpServletRequest: HttpServletRequest):
            ResponseEntity<Error> =
            handleExceptionImpl(e.message ?: "", httpServletRequest, HttpStatus.UNAUTHORIZED, AUTHENTICATION_ERROR,
                    setOf())

    /*
    when a non-null type param gets "null". nested exception is then a MissingKotlinParameterException
     */
    @ExceptionHandler(HttpMessageNotReadableException::class)
    fun handleHttpMessageNotReadableException(e: HttpMessageNotReadableException, httpServletRequest: HttpServletRequest):
            ResponseEntity<Error> {

        /*
        example of e.cause.msg:

        Instantiation of [simple type, class com.deliveryhero.services.crs.api.auth.UsernamePasswordCredentials] value
        failed for JSON property username due to missing (therefore NULL) value for creator parameter username which is
        a non-nullable type
         */

        var message: String? = null
        val cause = e.cause
        val constraintViolations = mutableSetOf<ConstraintViolation>()

        //TODO can there be another cause?
        if (cause is MissingKotlinParameterException) {

            // TODO can there be other cases than not-null for a MissingKotlinParameterException ?

            val matchResult = NOT_NULL_CREATOR_PATTERN.matchEntire(cause.msg)
            if (matchResult != null) {
                val fieldName = matchResult.groups[1]!!.value
                message = "Model validation failed."
                constraintViolations.add(ConstraintViolation(fieldName, "not-empty", "null",
                        "'$fieldName' may not be empty."))
            }
        }

        if (message == null) {
            message = e.message ?: "JSON was not readable."
        }

        return handleExceptionImpl(message, httpServletRequest, HttpStatus.BAD_REQUEST, MODEL_VALIDATION_FAILED,
                constraintViolations)
    }

    /*
    jsr validation issue
     */
    @ExceptionHandler(MethodArgumentNotValidException::class)
    fun handleMethodArgumentNotValidException(e: MethodArgumentNotValidException, httpServletRequest: HttpServletRequest):
            ResponseEntity<Error> {

        val constraintViolations = mutableSetOf<ConstraintViolation>()
        for (fieldError in e.bindingResult.fieldErrors) {

            var message = fieldError.defaultMessage
            var constraint = "unspecified"

            // TODO handle remaining constraints

            if (message == null || message.contains("must not be blank")) {
                message = "may not be empty"
                constraint = "not-empty"

            }

            constraintViolations.add(ConstraintViolation(fieldError.field, constraint,
                    fieldError.rejectedValue.toString(), "'${fieldError.field}' $message."))
        }

        return handleExceptionImpl("Model validation failed.", httpServletRequest, HttpStatus.BAD_REQUEST,
                MODEL_VALIDATION_FAILED, constraintViolations)
    }

    private fun handleExceptionImpl(message: String, httpServletRequest: HttpServletRequest, httpStatus: HttpStatus,
                                    code: String, constraintViolations: Set<ConstraintViolation>): ResponseEntity<Error> {

        val requestUrlBuffer = httpServletRequest.requestURL
        val queryString = httpServletRequest.queryString
        if (queryString != null) {
            requestUrlBuffer.append("?").append(queryString)
        }

        // TODO use real request id
        // TODO build constraint violations
        val error = Error(Instant.now(), requestUrlBuffer.toString(), UUID.randomUUID().toString(), code,
                message, constraintViolations)

        val headers = HttpHeaders().apply {
            contentType = MediaType.APPLICATION_JSON
        }

        return ResponseEntity(error, headers, httpStatus)
    }
}