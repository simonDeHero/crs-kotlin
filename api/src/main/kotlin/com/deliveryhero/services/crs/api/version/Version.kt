package com.deliveryhero.services.crs.api.version

import java.time.Instant

data class Version(
        val version: String,
        val revision: String,
        val timestamp: Instant,
        val buildNumber: String
)