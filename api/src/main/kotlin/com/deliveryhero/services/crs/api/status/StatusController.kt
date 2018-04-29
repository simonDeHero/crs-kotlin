package com.deliveryhero.services.crs.api.status

import org.springframework.http.MediaType
import org.springframework.web.bind.annotation.GetMapping

interface StatusController {

    companion object {
        const val PATH = "api/1/status"
    }

    @GetMapping(produces = [MediaType.APPLICATION_JSON_VALUE])
    fun getStatus(): Status
}