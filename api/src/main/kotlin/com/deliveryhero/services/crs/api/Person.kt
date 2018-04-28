package com.deliveryhero.services.crs.api

import javax.validation.constraints.Min
import javax.validation.constraints.NotNull

// TODO remove as it is not part of CRS, only for showcase of POST
data class Person(
        val name: String,
        @get:NotNull val title: String? = null,
        @get:Min(0) val age: Int
)