package com.deliveryhero.services.crs.api.delivery

data class Customer(
        val customerId: String,
        val phone: String? = null,
        val contactId: String? = null,
        val firstName: String? = null,
        val lastName: String? = null,
        val language: String? = null,
        val company: String? = null,
        val email: String? = null
)
