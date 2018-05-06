package com.deliveryhero.services.crs.api.error

data class ConstraintViolation(
        val field: String,
        val constraint: String,
        val invalidValue: String,
        val message: String
)