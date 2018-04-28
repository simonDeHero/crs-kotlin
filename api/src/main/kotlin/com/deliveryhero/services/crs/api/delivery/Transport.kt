package com.deliveryhero.services.crs.api.delivery

import java.time.Instant

data class Transport(
        val type: TransportType = TransportType.UNKNOWN,
        val transportName: String? = null,
        val transportJobId: String? = null,
        val driverId: String? = null,
        val driverName: String? = null,
        val pickupTime: Instant? = null,
        val dispatchedAt: Instant? = null,
        val deliveredAt: Instant? = null,
        val cancelReason: TransportCancelReason? = null
)