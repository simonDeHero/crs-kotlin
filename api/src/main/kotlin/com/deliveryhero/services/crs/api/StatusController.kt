package com.deliveryhero.services.crs.api

import org.springframework.web.bind.annotation.GetMapping

interface StatusController {

    companion object {
        const val PATH = "api/1/status"
    }

    @GetMapping
    fun getStatus(): String
}