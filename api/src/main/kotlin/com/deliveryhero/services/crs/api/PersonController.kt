package com.deliveryhero.services.crs.api

import org.springframework.http.MediaType
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody

// TODO remove as it is not part of CRS, only for showcase of POST
interface PersonController {

    companion object {
        const val PATH = "api/1/person"
    }

    @PostMapping(produces = [MediaType.APPLICATION_JSON_VALUE], consumes = [MediaType.APPLICATION_JSON_VALUE])
    fun create(@RequestBody person: Person): Person
}