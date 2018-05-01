package com.deliveryhero.services.crs.api

import io.swagger.annotations.ApiModelProperty
import java.time.Instant

/**
 * Represents error information about the current failed request.
 */
data class Error(

        @ApiModelProperty(notes = "The ISO-8601 timestamp representing the time, the request was processed.",
                required = true, readOnly = true, allowEmptyValue = false, example = "2016-06-01T15:00:01.010Z")
        val timestamp: Instant,

        @ApiModelProperty(notes = "The origin URL of the request.", required = true, readOnly = true,
                allowEmptyValue = false, example = "http://www.example.org/pet-store/api/1/customer/666")
        val requestUrl: String,

        @ApiModelProperty(notes = "The unique id of the request. Can be used within support requests.", required = true,
                readOnly = true, allowEmptyValue = false, example = "1175d6e0210411e6b20c0002a5d5c51b")
        val requestId: String,

        @ApiModelProperty(notes = "The error code. Can be used on client side to check for specific error cases.",
                required = true, readOnly = true, allowEmptyValue = false, example = "model-validation-error")
        val code: String,

        @ApiModelProperty(notes = "The technical, english only, human readable, error message.", required = true,
                readOnly = true, allowEmptyValue = false,
                example = "Model validation failed. See constraint violations for more details.")
        val message: String,

        @ApiModelProperty(notes = "The constraint violations that caused this error if available.", readOnly = true,
                allowEmptyValue = true)
        val constraintViolations: Set<*>?
)