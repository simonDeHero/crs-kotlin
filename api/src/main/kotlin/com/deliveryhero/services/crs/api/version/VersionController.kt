package com.deliveryhero.services.crs.api.version

import org.springframework.http.MediaType
import org.springframework.web.bind.annotation.GetMapping

interface VersionController {

    companion object {
        const val PATH = "api/1/version"
    }

    @GetMapping(produces = [MediaType.APPLICATION_JSON_VALUE])
    fun getVersion(): Version
}